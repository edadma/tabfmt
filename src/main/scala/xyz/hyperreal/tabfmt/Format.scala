package xyz.hyperreal.tabfmt

import java.io.{File, FileWriter}

import xyz.hyperreal.importer.{Column, Importer}
import xyz.hyperreal.table.TextTable

object Format {

  def apply(inpath: String, outpath: String): Unit = {
    val file = new File(inpath)

    if (file.exists && file.isFile && file.canRead) {
      try {
        val s       = io.Source.fromFile(file)
        val content = s.mkString

        s.close

        val tables = Importer.importFromString(content, doubleSpaces = true)
        val buf    = new StringBuilder

        def put(s: String) = buf ++= s

        def putln(s: String) = put(s"$s\n")

        for (t <- tables) {
          if (t ne tables.head)
            put("\n")

          putln(t.name)

          val table = new TextTable { noansi }

          table.headerSeq(t.header map {
            case Column(name, typ, Nil)  => s"$name: $typ"
            case Column(name, typ, args) => s"$name: $typ, ${args mkString ", "}"
          })

          for (r <- t.data)
            table.rowSeq(r)

          put(table.toString)
        }

        if (content != buf.toString) {
          val out = new FileWriter(outpath)

          out.write(buf.toString)
          out.close
        }
      } catch {
        case ex: Exception =>
          println(ex.getMessage)
          sys.exit(1)
      }
    } else {
      println(s"file '$inpath' is unreadable")
      sys.exit(1)
    }
  }

}
