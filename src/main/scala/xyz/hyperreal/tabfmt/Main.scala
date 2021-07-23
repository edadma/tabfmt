package xyz.hyperreal.tabfmt

object Main extends App {

  args match {
    case Array(path) =>
      if (path endsWith ".tab")
        Format(path, path)
    case Array(path, out) => Format(path, out)
    case _ =>
      println("missing arguments")
      sys.exit(1)
  }

}
