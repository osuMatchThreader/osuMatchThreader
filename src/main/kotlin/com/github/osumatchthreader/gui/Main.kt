package com.github.osumatchthreader.gui

import com.github.osumatchthreader.parser.MatchSeriesParser
import com.github.osumatchthreader.resourceAsString
import com.github.osumatchthreader.stackTraceToString
import com.github.osumatchthreader.toMarkdown
import javafx.application.Platform
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.*
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import javafx.stage.Stage
import org.jsoup.Jsoup
import java.awt.Desktop
import java.io.File
import java.net.URI
import kotlin.concurrent.thread

val loadingScene = Scene(StackPane(Text("Loading...")))

data class SettableTextTabPane(
    val pane: Pane,
    val textArea: TextArea,
    val resetButton: Button
) {
  fun bindTo(tab: Tab) = pane.let { tab.content = it }
}

fun settableTextPane(name: String, stage: Stage): SettableTextTabPane {
  val defaultText = resourceAsString("default$name")
  val file = System.getProperty("user.home")
      .let { File(it, "osu! Match Thread Generator") }
      .apply { mkdirs() }
      .let { File(it, "$name.txt") }
  val textArea = TextArea(when (file.exists()) {
    true -> file.readText()
    false -> defaultText
  }).apply { VBox.setVgrow(this, Priority.ALWAYS) }

  val pane = VBox()
  val resetButton = Button("Reset $name").addTo(pane).apply {
    setOnAction { textArea.text = defaultText }
  }
  textArea.addTo(pane)
  stage.setOnCloseRequest { file.writeText(textArea.text) }

  return SettableTextTabPane(pane, textArea, resetButton)
}

fun Stage.setupPrimaryStage(): Unit = try {
  title = "osu!MatchThreader"
  scene = loadingScene
  show()

  val tabPane = TabPane()
  fun newTab(name: String) = Tab(name).apply {
    isClosable = false
    tabPane.tabs.add(this)
  }

  val inputTab = newTab("Input")
  val outputTab = newTab("Output")
  val headerTab = newTab("Header")
  val footerTab = newTab("Footer")

  // Required by input tab, declaring earlier
  val sortToggleGroup = ToggleGroup()
  val headerTabPane = settableTextPane("Header", this)
  val footerTabPane = settableTextPane("Footer", this)
  val outputTextArea = TextArea().apply { isEditable = false }

  // Setup input tab
  val inputTabPane = VBox()
  val textHBox = HBox().addTo(inputTabPane)
  fun newTextField(name: String, addTo: Pane = textHBox): TextField =
      TextField(name).addTo(addTo).apply { HBox.setHgrow(this, Priority.ALWAYS) }

  val warmupMatchesTextField = newTextField("Warmup matches")
  val blueTeamNameTextField = newTextField("Blue team name")
  val redTeamNameTextField = newTextField("Red team name")
  val matchUrlTextField = newTextField("Match URL", inputTabPane)

  val sortByTabPane = HBox().addTo(inputTabPane).apply { spacing = 4.0 }
  Label("Sort score table by:").addTo(sortByTabPane)
  listOf(
      Pair(SortMode.TEAM_THEN_SCORE, "W. Team > L. Team (score)"),
      Pair(SortMode.SCORE, "Score"),
      Pair(SortMode.SLOTS, "Slots"),
      Pair(SortMode.TEAM_THEN_SLOTS, "W. Team > L. Team (slots)")
  ).forEachIndexed { i, p ->
    RadioButton(p.second).apply {
      if (i == 0) isSelected = true
      userData = p.first
      toggleGroup = sortToggleGroup
      addTo(sortByTabPane)
    }
  }

  fun userInput() = UserInput(
      warmupMatchesTextField.text.toLong(),
      blueTeamNameTextField.text,
      redTeamNameTextField.text,
      matchUrlTextField.text,
      sortToggleGroup.selectedToggle.userData as SortMode,
      headerTabPane.textArea.text,
      footerTabPane.textArea.text
  )

  val convertButton = Button("Go!").addTo(inputTabPane)
  convertButton.setOnAction {
    convertButton.isDisable = true
    val oldLabel = convertButton.text
    fun output(text: String) {
      convertButton.isDisable = false
      convertButton.text = oldLabel
      outputTextArea.text = text
      tabPane.selectionModel.select(outputTab)
    }

    val userInput = try {
      userInput()
    } catch (e: Exception) {
      return@setOnAction output(e.stackTraceToString())
    }

    convertButton.text = "Fetching data..."
    outputTextArea.text = ""
    thread {
      val text = try {
        val mpElement = Jsoup.connect(userInput.mpUrl).get().select("div.content-with-bg").first()
        Platform.runLater { convertButton.text = "Converting to markup..." }
        val markdown = MatchSeriesParser.newInstance().parse(mpElement).toMarkdown(userInput)
        "${userInput.header}$markdown${userInput.footer.replace("{URL}", userInput.mpUrl)}"
      } catch (e: Exception) {
        Exception("Failed to parse/convert data from ${userInput.mpUrl}", e)
            .stackTraceToString()
      }

      Platform.runLater { output(text) }
    }
  }

  val infoPane = VBox().apply { alignment = Pos.CENTER }

  fun label(text: String): Label = Label(text)
  fun url(uri: String, protocol: String = "http://"): Hyperlink {
    val _uri = URI("$protocol$uri")
    val link = Hyperlink(uri)
    link.setOnAction { Desktop.getDesktop().browse(_uri) }
    return link
  }

  operator fun Labeled.plus(other: Labeled) = TextFlow(this, other)
  fun spacer() = label("")
  listOf(
      label("osu!MatchThreader ${resourceAsString("version")}"),
      spacer(),
      label("GitHub repository:") + url("www.github.com/osuMatchThreader/osuMatchThreader"),
      label("Contact via reddit:") + url("www.reddit.com/u/osuMatchThreader"),
      label("Contact via email:") + url("osumatchthreader@gmail.com", "mailto:"),
      spacer(),
      label("This program is distributed under the MIT License, see") + url("www.opensource.org/licenses/MIT")
  ).forEach { it.addTo(infoPane) }
  infoPane.addTo(inputTabPane)

  inputTab.content = inputTabPane
  outputTab.content = outputTextArea
  headerTabPane.bindTo(headerTab)
  footerTabPane.bindTo(footerTab)

  Platform.runLater { scene = Scene(tabPane) }
} catch (e: Exception) {
  e.printStackTrace(System.out)
}