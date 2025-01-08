package com.driverspa.util.otto.ws;

import java.io.File;

public class ClientPhotoPostRequestEvent {

	private File file;
	
	public ClientPhotoPostRequestEvent (File file) {
		this.file = file;
	}

	public File getFile() {
		return file;
	}
		
}
