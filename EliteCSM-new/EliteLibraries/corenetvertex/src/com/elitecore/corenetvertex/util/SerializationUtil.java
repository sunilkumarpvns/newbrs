package com.elitecore.corenetvertex.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SerializationUtil {

	public static void serialize(Object object, String fileName) throws IOException {
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName));
		try {
			oos.writeObject(object);
		} finally {
			oos.close();
		}
	}

	public static Object deserialize(String fileName) throws IOException, ClassNotFoundException {
		
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName));
		try {
			return ois.readObject();
		} finally {
			ois.close();
		}
	}
}
