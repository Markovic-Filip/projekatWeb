package app;

import static spark.Spark.port;
import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.staticFiles;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.google.gson.Gson;

import beans.Domacin;
import beans.Gost;
import beans.Korisnik;

public class MainApp {

	private static Gson gson = new Gson();
	private static ArrayList<Korisnik> korisnici = new ArrayList<Korisnik>();
	
	public static void main(String[] args) throws IOException {

		port(8080);
		
		staticFiles.externalLocation(new File("./static").getCanonicalPath());
		
		post("/app/registracija/domacin", (req, res) ->	{
			res.type("application/json");
			String payload = req.body();
			Domacin noviDomacin = gson.fromJson(payload, Domacin.class);
			if (noviDomacin != null)
				korisnici.add(noviDomacin);
			else
				return "{'ime': 'GRESKA'}";
			return gson.toJson(noviDomacin);
		});
		
		post("/app/registracija/gost", (req, res) -> {
			res.type("application/json");
			String payload = req.body();
			//System.out.println(payload);
			Gost noviGost = gson.fromJson(payload, Gost.class);
			if (noviGost != null)	{
				//System.out.println(noviGost.getIme() + ", " + noviGost.getPrezime() + ", " + noviGost.getLozinka());
				// TODO: da li postoji korisnik sa istim korisnickim imenom?
				korisnici.add(noviGost);
			}
			else
				// TODO: ovo ne valja, mozda return null pa catch error u axios pozivu?
				return "{'ime': 'GRESKA'}";
			//return gson.toJson(noviGost);
			return noviGost;
		});
	}

}
