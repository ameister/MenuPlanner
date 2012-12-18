package ch.bbv.control;

import java.util.List;

import javafx.util.StringConverter;
import ch.bbv.bo.Condiment;

import com.google.common.base.Strings;

public class CondimentConverter extends StringConverter<Condiment> {
	final List<Condiment> data;
	
	public CondimentConverter(List<Condiment> data) {
		this.data = data;
	}

	@Override
	public String toString(Condiment condiment) {
		if (condiment == null) {
			return null;
		}
		return condiment.toString();
	}

	@Override
	public Condiment fromString(String string) {
		if (!Strings.isNullOrEmpty(string)) {
			Condiment condiment = new Condiment(string);
			data.add(condiment);
			return condiment;
		}
		return null;
	}
}