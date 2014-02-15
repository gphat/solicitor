package solicitor

object Thing {

  def isEnabled(name: String): Boolean = {
    true
  }

  def isDisabled(name: String): Boolean = !isEnabled(name)

  def getValue(name: String): String = {
    "fadsd"
  }
}