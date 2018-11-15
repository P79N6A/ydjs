package com.yd.ydsp.common.redis;

import java.io.*;

public class SerializeUtils {
	
	/**
	 * 反序列化
	 * @param bytes
	 * @return
	 */
	public static Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
		
		Object result = null;
		
		if (isEmpty(bytes)) {
			return null;
		}

		ByteArrayInputStream byteStream = new ByteArrayInputStream(bytes);

		ObjectInputStream objectInputStream = new ObjectInputStream(byteStream);

		result = objectInputStream.readObject();
		return result;
	}
	
	public static boolean isEmpty(byte[] data) {
		return (data == null || data.length == 0);
	}

	/**
	 * 序列化
	 * @param object
	 * @return
	 */
	public static byte[] serialize(Object object) throws IOException {
		
		byte[] result = null;
		
		if (object == null) {
			return new byte[0];
		}

			ByteArrayOutputStream byteStream = new ByteArrayOutputStream(128);

            if (!(object instanceof Serializable)) {
                throw new IllegalArgumentException(SerializeUtils.class.getSimpleName() + " requires a Serializable payload " +
                        "but received an object of type [" + object.getClass().getName() + "]");
            }
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteStream);
            objectOutputStream.writeObject(object);
            objectOutputStream.flush();
            result =  byteStream.toByteArray();

		return result;
	}
}
