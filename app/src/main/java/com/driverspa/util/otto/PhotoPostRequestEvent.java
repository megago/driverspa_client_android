package com.driverspa.util.otto;

import java.io.File;

public class PhotoPostRequestEvent {
	private File file;
	
	public PhotoPostRequestEvent (File file) {
		this.file = file;
	}

	public File getFile() {
		return file;
	}

}
