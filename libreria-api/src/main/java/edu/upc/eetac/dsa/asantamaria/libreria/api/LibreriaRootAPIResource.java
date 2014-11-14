package edu.upc.eetac.dsa.asantamaria.libreria.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import edu.upc.eetac.dsa.asantamaria.libreria.api.model.LibreriaRootAPI;

@Path("/")
public class LibreriaRootAPIResource {
	@GET
	public LibreriaRootAPI getRootAPI() {
		LibreriaRootAPI api = new LibreriaRootAPI();
		return api;
	}
}