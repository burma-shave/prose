package config

import play.api.db.slick.DB
import scala.slick.driver.ExtendedProfile
import scala.slick.session.Database

import config.slick.Profile

/**
 * Created with IntelliJ IDEA.
 * User: ericj
 * Date: 12/04/13
 * Time: 09:31
 * To change this template use File | Settings | File Templates.
 */
trait CurrentPlayDb extends Profile {
  import play.api.Play.current

  override val profile: ExtendedProfile = DB.driver(play.api.Play.current)
  override val db: Database = Database.forDataSource(play.api.db.DB.getDataSource())
}

trait TestDb extends Profile {
  import scala.slick.driver.H2Driver

  override val profile: ExtendedProfile = H2Driver
  override val db: Database = Database.forURL("jdbc:h2:mem:test1", driver = "org.h2.Driver")

}

