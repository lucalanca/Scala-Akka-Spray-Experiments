package domain.interfaces

trait IEvent {
  var countryCode : String
  var eventName   : String
  var openDate    : String
  var timezone    : String
  var venue       : String
  var eventId     : Int
 }
