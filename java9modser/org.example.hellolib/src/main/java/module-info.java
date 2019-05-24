module org.example.hellolib {
	exports org.example.hellolib;

	provides java.util.function.IntSupplier with org.example.hellolib.MyIntSupplier;
}