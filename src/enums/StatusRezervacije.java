package enums;

import com.google.gson.annotations.SerializedName;

public enum StatusRezervacije {
	@SerializedName("0")
	KREIRANA,
	@SerializedName("1")
	ODBIJENA,
	@SerializedName("2")
	ODUSTANAK,
	@SerializedName("3")
	PRIHVACENA,
	@SerializedName("4")
	ZAVRSENA
}
