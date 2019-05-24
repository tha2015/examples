package org.example.hellolib;

import java.util.Random;
import java.util.function.IntSupplier;

public class MyIntSupplier implements IntSupplier {

	@Override
	public int getAsInt() {
		return new Random().nextInt();
	}

}
