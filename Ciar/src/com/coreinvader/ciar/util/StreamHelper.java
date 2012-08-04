package com.coreinvader.ciar.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;

public class StreamHelper {

    /**
     * To convert the InputStream to String we use the Reader.read(char[] buffer) method. We iterate
     * until the Reader return -1 which means there's no more data to read. We use the StringWriter
     * class to produce the string.
     */
    public static String makeString(InputStream is) throws IOException {
	if (is != null) {
	    Writer writer = new StringWriter();

	    char[] buffer = new char[1024];
	    try {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		int n;
		while ((n = reader.read(buffer)) != -1) {
		    writer.write(buffer, 0, n);
		}
	    } finally {
		is.close();
	    }

	    return writer.toString();
	} else
	    return "";
    }
    
    public static InputStream cloneStream(InputStream is) throws IOException {
	ByteArrayOutputStream baos = new ByteArrayOutputStream();

	byte[] buffer = new byte[1024];
	int len;
	while ((len = is.read(buffer)) > 0) {
	    baos.write(buffer, 0, len);
	}
	baos.flush();

	return new ByteArrayInputStream(baos.toByteArray());
    }
}
