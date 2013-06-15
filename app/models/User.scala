package models

/**
 * @author Valentin Kasas
 */
case class User( id: String,
                 email: String,
                 publicKey : String) {

}

object User {

  def find(username : String) = User("someid", username, "publicKey")
}