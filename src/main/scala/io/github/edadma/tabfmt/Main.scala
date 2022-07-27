package io.github.edadma.tabfmt

@main def run(ps: String*): Unit =
  ps match {
    case Seq(path) =>
      if (path endsWith ".tab")
        Format(path, path)
    case Seq(path, out) => Format(path, out)
    case _ =>
      println("missing arguments")
      sys.exit(1)
  }
