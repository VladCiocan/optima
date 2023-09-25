package com.hartehanks;

public class PatternDefinition
{
	String patGender;
	String countryIso;
	String patternId;
	String[] leftIterate;
	String[] rightIterate;

	public PatternDefinition(String patGender, String countryIso,
		String patternId)
	{
	    this.patGender = patGender;
	    this.countryIso = countryIso;
	    this.patternId = patternId;
	}
}
