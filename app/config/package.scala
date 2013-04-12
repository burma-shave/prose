package config

import scala.slick.driver.ExtendedProfile
import scala.slick.session.Database

/**
 * Created with IntelliJ IDEA.
 * User: ericj
 * Date: 12/04/13
 * Time: 09:38
 * To change this template use File | Settings | File Templates.
 */
package object slick {
  trait Profile {
    val profile: ExtendedProfile
    val db: Database
  }
}
