package models.slick


import play.api.db.slick.Profile
import scala.slick.driver.ExtendedProfile
import play.api.db.slick.DB


/**
 * Author: Eric Jutrzenka
 * Date:   07/04/2013
 * Time:   18:18
 */

trait BlogSlickComponent { this: Profile =>

  import profile.simple._

  object Users extends Table[(String, String, String)]("USERS") {
    def id = column[String]("ID", O.PrimaryKey) // This is the primary key column
    def firstName = column[String]("FIRSTNAME")
    def surName = column[String]("SURNAME")
    // Every table needs a * projection with the same type as the table's type parameter
    def * = id ~ firstName ~ surName
  }

  object Articles extends Table[(String, String, String, String)]("ARTICLES") {
    def id = column[String]("ID", O.PrimaryKey) // This is the primary key column
    def authorId = column[String]("AUTHOR_ID")
    def title = column[String]("TITLE")
    def body = column[String]("BODY")
    def * = id ~ authorId ~ title ~ body
  }


}

class DAO(override val profile: ExtendedProfile) extends BlogSlickComponent with Profile

object current {
  val db = DB("default")
  val dao = new DAO(db.driver(play.api.Play.current))
}