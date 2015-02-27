package net.tc.lang;

public interface Language
{

	public static String ENGLISH = "en";
	public static String FRENCH = "fr";
	public static String SPANISH = "es";
	public static String ARABIC = "ar";
	public static String CHINESE = "zh";
	public static String[] alowedLanguages = new String[]{"en", "fr", "es", "ar", "zh"};

	public static String DEFAULTLANG = ENGLISH;

	public String getLanguage();
	public String[] getLanguages();
	public String getCurrentLanguage();
	public void setCurrentLanguage(String lang);

}
