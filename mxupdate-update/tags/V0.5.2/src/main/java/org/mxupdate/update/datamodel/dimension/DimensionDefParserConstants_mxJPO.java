/* Generated By:JavaCC: Do not edit this line. DimensionDefParserConstants.java */
package org.mxupdate.update.datamodel.dimension;


/**
 * Token literal values and constants.
 * Generated by org.javacc.parser.OtherFilesGen#start()
 */
public interface DimensionDefParserConstants_mxJPO {

  /** End of File. */
  int EOF = 0;
  /** RegularExpression Id. */
  int DESCRIPTION = 5;
  /** RegularExpression Id. */
  int HIDDEN = 6;
  /** RegularExpression Id. */
  int UNIT = 7;
  /** RegularExpression Id. */
  int HIDDEN_BOOLEAN_TRUE = 12;
  /** RegularExpression Id. */
  int HIDDEN_BOOLEAN_FALSE = 13;
  /** RegularExpression Id. */
  int HIDDEN_BOOLEAN_TRUESTR = 14;
  /** RegularExpression Id. */
  int HIDDEN_BOOLEAN_FALSESTR = 15;
  /** RegularExpression Id. */
  int DESCRIPTION_STRING = 20;
  /** RegularExpression Id. */
  int DESCRIPTION_SINGLE = 21;
  /** RegularExpression Id. */
  int DESCRIPTION_APOSTROPHE = 22;
  /** RegularExpression Id. */
  int DESCRIPTION_CHAR = 23;
  /** RegularExpression Id. */
  int UNITNAME_STRING = 28;
  /** RegularExpression Id. */
  int UNITNAME_SINGLE = 29;
  /** RegularExpression Id. */
  int UNITNAME_APOSTROPHE = 30;
  /** RegularExpression Id. */
  int UNITNAME_CHAR = 31;
  /** RegularExpression Id. */
  int UNITDEF_START = 36;
  /** RegularExpression Id. */
  int UNITDEF_END = 37;
  /** RegularExpression Id. */
  int UNITDEF_DEFAULT = 38;
  /** RegularExpression Id. */
  int UNITDEF_DESCRIPTION = 39;
  /** RegularExpression Id. */
  int UNITDEF_LABEL = 40;
  /** RegularExpression Id. */
  int UNITDEF_MULTIPLIER = 41;
  /** RegularExpression Id. */
  int UNITDEF_OFFSET = 42;
  /** RegularExpression Id. */
  int UNITDEF_SETTING = 43;
  /** RegularExpression Id. */
  int UNITDEF_PROPERTY = 44;
  /** RegularExpression Id. */
  int UNITDEF_PROPERTYVAL = 45;
  /** RegularExpression Id. */
  int UNITDEF_PROPERTYTO = 46;
  /** RegularExpression Id. */
  int UNITDEF_SYSTEM = 47;
  /** RegularExpression Id. */
  int UNITDEF_BOOLEAN_TRUE = 52;
  /** RegularExpression Id. */
  int UNITDEF_BOOLEAN_FALSE = 53;
  /** RegularExpression Id. */
  int UNITDEF_BOOLEAN_TRUESTR = 54;
  /** RegularExpression Id. */
  int UNITDEF_BOOLEAN_FALSESTR = 55;
  /** RegularExpression Id. */
  int UNITDEF_STRING_STRING = 60;
  /** RegularExpression Id. */
  int UNITDEF_STRING_SINGLE = 61;
  /** RegularExpression Id. */
  int UNITDEF_STRING_APOSTROPHE = 62;
  /** RegularExpression Id. */
  int UNITDEF_STRING_CHAR = 63;
  /** RegularExpression Id. */
  int UNITDEF_STRINGSTRING_STRING = 68;
  /** RegularExpression Id. */
  int UNITDEF_STRINGSTRING_SINGLE = 69;
  /** RegularExpression Id. */
  int UNITDEF_STRINGSTRING_APOSTROPHE = 70;
  /** RegularExpression Id. */
  int UNITDEF_STRINGSTRING_CHAR = 71;
  /** RegularExpression Id. */
  int UNITDEF_DOUBLE_DOUBLE = 76;
  /** RegularExpression Id. */
  int UNITDEF_SYSTEMNAME_STRING = 81;
  /** RegularExpression Id. */
  int UNITDEF_SYSTEMNAME_SINGLE = 82;
  /** RegularExpression Id. */
  int UNITDEF_SYSTEMNAME_APOSTROPHE = 83;
  /** RegularExpression Id. */
  int UNITDEF_SYSTEMNAME_CHAR = 84;
  /** RegularExpression Id. */
  int UNITDEF_SYSTEMDEF_TO = 89;
  /** RegularExpression Id. */
  int UNITDEF_SYSTEMDEF_UNIT = 90;
  /** RegularExpression Id. */
  int UNITDEF_SYSTEMUNIT_STRING = 95;
  /** RegularExpression Id. */
  int UNITDEF_SYSTEMUNIT_SINGLE = 96;
  /** RegularExpression Id. */
  int UNITDEF_SYSTEMUNIT_APOSTROPHE = 97;
  /** RegularExpression Id. */
  int UNITDEF_SYSTEMUNIT_CHAR = 98;

  /** Lexical state. */
  int DEFAULT = 0;
  /** Lexical state. */
  int HIDDEN_EXCPECTED = 1;
  /** Lexical state. */
  int DESCRIPTION_EXPECTED = 2;
  /** Lexical state. */
  int UNITNAME_EXPECTED = 3;
  /** Lexical state. */
  int UNITDEF_EXPECTED = 4;
  /** Lexical state. */
  int UNITDEF_BOOLEAN_EXPECTED = 5;
  /** Lexical state. */
  int UNITDEF_STRING_EXPECTED = 6;
  /** Lexical state. */
  int UNITDEF_STRINGSTRING_EXPECTED = 7;
  /** Lexical state. */
  int UNITDEF_DOUBLE_EXPECTED = 8;
  /** Lexical state. */
  int UNITDEF_SYSTEMNAME_EXPECTED = 9;
  /** Lexical state. */
  int UNITDEF_SYSTEMDEF_EXPECTED = 10;
  /** Lexical state. */
  int UNITDEF_SYSTEMUNIT_EXPECTED = 11;

  /** Literal token values. */
  String[] tokenImage = {
    "<EOF>",
    "\" \"",
    "\"\\r\"",
    "\"\\t\"",
    "\"\\n\"",
    "\"description\"",
    "\"hidden\"",
    "\"unit\"",
    "\" \"",
    "\"\\t\"",
    "\"\\n\"",
    "\"\\r\"",
    "<HIDDEN_BOOLEAN_TRUE>",
    "<HIDDEN_BOOLEAN_FALSE>",
    "<HIDDEN_BOOLEAN_TRUESTR>",
    "<HIDDEN_BOOLEAN_FALSESTR>",
    "\" \"",
    "\"\\t\"",
    "\"\\n\"",
    "\"\\r\"",
    "<DESCRIPTION_STRING>",
    "<DESCRIPTION_SINGLE>",
    "\"\\\"\"",
    "<DESCRIPTION_CHAR>",
    "\" \"",
    "\"\\t\"",
    "\"\\n\"",
    "\"\\r\"",
    "<UNITNAME_STRING>",
    "<UNITNAME_SINGLE>",
    "\"\\\"\"",
    "<UNITNAME_CHAR>",
    "\" \"",
    "\"\\t\"",
    "\"\\n\"",
    "\"\\r\"",
    "\"{\"",
    "\"}\"",
    "\"default\"",
    "\"description\"",
    "\"label\"",
    "\"multiplier\"",
    "\"offset\"",
    "\"setting\"",
    "\"property\"",
    "\"value\"",
    "\"to\"",
    "\"system\"",
    "\" \"",
    "\"\\t\"",
    "\"\\n\"",
    "\"\\r\"",
    "<UNITDEF_BOOLEAN_TRUE>",
    "<UNITDEF_BOOLEAN_FALSE>",
    "<UNITDEF_BOOLEAN_TRUESTR>",
    "<UNITDEF_BOOLEAN_FALSESTR>",
    "\" \"",
    "\"\\t\"",
    "\"\\n\"",
    "\"\\r\"",
    "<UNITDEF_STRING_STRING>",
    "<UNITDEF_STRING_SINGLE>",
    "\"\\\"\"",
    "<UNITDEF_STRING_CHAR>",
    "\" \"",
    "\"\\t\"",
    "\"\\n\"",
    "\"\\r\"",
    "<UNITDEF_STRINGSTRING_STRING>",
    "<UNITDEF_STRINGSTRING_SINGLE>",
    "\"\\\"\"",
    "<UNITDEF_STRINGSTRING_CHAR>",
    "\" \"",
    "\"\\t\"",
    "\"\\n\"",
    "\"\\r\"",
    "<UNITDEF_DOUBLE_DOUBLE>",
    "\" \"",
    "\"\\t\"",
    "\"\\n\"",
    "\"\\r\"",
    "<UNITDEF_SYSTEMNAME_STRING>",
    "<UNITDEF_SYSTEMNAME_SINGLE>",
    "\"\\\"\"",
    "<UNITDEF_SYSTEMNAME_CHAR>",
    "\" \"",
    "\"\\t\"",
    "\"\\n\"",
    "\"\\r\"",
    "\"to\"",
    "\"unit\"",
    "\" \"",
    "\"\\t\"",
    "\"\\n\"",
    "\"\\r\"",
    "<UNITDEF_SYSTEMUNIT_STRING>",
    "<UNITDEF_SYSTEMUNIT_SINGLE>",
    "\"\\\"\"",
    "<UNITDEF_SYSTEMUNIT_CHAR>",
  };

}
