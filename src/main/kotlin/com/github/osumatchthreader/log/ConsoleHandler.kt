package com.github.osumatchthreader.log

class ConsoleHandler : java.util.logging.ConsoleHandler() {
  init { setOutputStream(System.out) }
}