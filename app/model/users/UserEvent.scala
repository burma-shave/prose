package model.users

trait UserEvent {
  val id: String
}

case class UserCreated(id: String)
