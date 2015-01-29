# RxExtended
Extension to the Netflix Rx Framework

Overview
--------

Usage
-----

```scala

def toJson[T](t:T):JsObject = ...

  def main() = {
    implicit val system = ActorSystem("test")

    val jsonStream = Source.createTcpSource(new InetSocketAddress("localhost", 10310)).storeMap[String] {
      val elements: Seq[String] = _.decodeString("UTF-8").split("\n").toSeq
      if(isLastComplete(elements)) (elements, ByteString())
      else (elements.dropRight(1), ByteString(elements.last))
    }.map(toJson(_))
  }

```

Source
------

Monoid
------

Divider
-------
