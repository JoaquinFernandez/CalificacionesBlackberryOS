package calificaciones;

import net.rim.device.api.util.Persistable;

public class Storage implements Persistable {

	public String[] userinfo;
	public int cont = 0;
	public byte[] data;
	public boolean storedGPRS = false;
	public boolean storeUserInfo = false;
	public Storage () {}
}
