/**
 * Author: Eric Jutrzenka
 * Date:   15/03/2013
 * Time:   20:44
 */

package models

import java.util.UUID
import models.slick.SlickPersistenceComponent

trait UserRepository {
  def create(id: UUID, firstName: String, surname: String)
}

trait ArticleRepository {
  def create(id: UUID, author: UUID, title: String, body: String)
}

trait PersistenceComponent {
  val userRepository: UserRepository
  val articleRepository: ArticleRepository
}