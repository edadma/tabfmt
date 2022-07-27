package io.github.edadma.tabfmt

import java.io.{File, FileWriter}
import io.github.edadma.importer.{Column, Enum, Import, Importer}
import io.github.edadma.table.TextTable

import scala.collection.mutable

object Format {

  def apply(inpath: String, outpath: String): Unit = {
    val file = new File(inpath)

    if (file.exists && file.isFile && file.canRead) {
      try {
        val s       = scala.io.Source.fromFile(file)
        val content = s.mkString

        s.close

        val Import(enums, tables) = Importer.importFromString(content, doubleSpaces = true)
        val buf    = new mutable.StringBuilder

        def put(s: String) = buf ++= s

        def putln(s: String) = put(s"$s\n")

        def putlf = put("\n")

        for(Enum(name, labels) <- enums) {
          putln(s"$name: ${labels mkString ", "}")
          putlf
        }

        for (t <- tables) {
          if (t ne tables.head)
            putlf

          putln(t.name)

          val table = new TextTable { noansi() }

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
