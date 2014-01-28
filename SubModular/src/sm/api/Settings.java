package sm.api;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

public class Settings {
	public Settings(){
		final String authUser = "123050045";
		final String authPassword = "legend4635211$";
		Authenticator.setDefault(
		   new Authenticator() {
		      public PasswordAuthentication getPasswordAuthentication() {
		         return new PasswordAuthentication(
		               authUser, authPassword.toCharArray());
		      }
		   }
		);
		System.getProperties().put("proxySet", "true");
		System.getProperties().put("http.proxySet", "true");
		System.getProperties().put("http.proxyHost", "netmon.iitb.ac.in");
		System.getProperties().put("http.proxyPort", "80");
		System.getProperties().put("http.proxyUser", authUser);
		System.getProperties().put("http.proxyPassword", authPassword);
	}
	
	static{
		new Settings();
	}
}
