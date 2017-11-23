class Hello {

	@JsName(name="sayHello")
	fun sayHello(name: String):String {
		return "Hello!" + name
	}

}