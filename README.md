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
The library provide a Source object which can be used to create observable from different source.

Source Methods:

Monoid
------
A monoid is a type class which types have 2 properties:

- Having an identity element
- Be composible and the product of the composition is an element of the same type.

BufferedObserver
----------------
The concept behind the BufferedObserver is to create an observer that can "bufferize" the data coming from another
observer and eventually emit new events built from the source observer.

A BufferedObserver can be built from an Observer[R] where R:Monoid.

The BufferedObserver provides the following functions:

```scala
def storeMap[T](f: R => (Seq[T], R): Observable[T]
```

The function 'f' has the following semantic:

for each R element provided as input StoreMap will try to transform R into a Sequence of T, for each T in the sequence the new Observable[T]
will emit an event of T to its observer.
The second element of the output tuple of the function f is the "rest" from the tranformation operation, this will represent the part which will
be stored within the observable and aggregated with the new R coming from the source Observer[R]. The BufferedObservable will merge the rest with
the new R event coming from the source Observer because R:Monoid so its define a function of type:
```scala
def combine(R, R): R
```
For more details see the definition of Monoid.