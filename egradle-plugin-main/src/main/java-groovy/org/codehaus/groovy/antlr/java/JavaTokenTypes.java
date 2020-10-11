/*
 * Copyright 2016 Albert Tregnaghi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 */
// $ANTLR 2.7.7 (20060906): "java.g" -> "JavaLexer.java"$

package org.codehaus.groovy.antlr.java;

public interface JavaTokenTypes {
    int EOF = 1;
    int NULL_TREE_LOOKAHEAD = 3;
    int BLOCK = 4;
    int MODIFIERS = 5;
    int OBJBLOCK = 6;
    int SLIST = 7;
    int METHOD_DEF = 8;
    int VARIABLE_DEF = 9;
    int INSTANCE_INIT = 10;
    int STATIC_INIT = 11;
    int TYPE = 12;
    int CLASS_DEF = 13;
    int INTERFACE_DEF = 14;
    int PACKAGE_DEF = 15;
    int ARRAY_DECLARATOR = 16;
    int EXTENDS_CLAUSE = 17;
    int IMPLEMENTS_CLAUSE = 18;
    int PARAMETERS = 19;
    int PARAMETER_DEF = 20;
    int LABELED_STAT = 21;
    int TYPECAST = 22;
    int INDEX_OP = 23;
    int POST_INC = 24;
    int POST_DEC = 25;
    int METHOD_CALL = 26;
    int EXPR = 27;
    int ARRAY_INIT = 28;
    int IMPORT = 29;
    int UNARY_MINUS = 30;
    int UNARY_PLUS = 31;
    int CASE_GROUP = 32;
    int ELIST = 33;
    int FOR_INIT = 34;
    int FOR_CONDITION = 35;
    int FOR_ITERATOR = 36;
    int EMPTY_STAT = 37;
    int FINAL = 38;
    int ABSTRACT = 39;
    int STRICTFP = 40;
    int SUPER_CTOR_CALL = 41;
    int CTOR_CALL = 42;
    int VARIABLE_PARAMETER_DEF = 43;
    int STATIC_IMPORT = 44;
    int ENUM_DEF = 45;
    int ENUM_CONSTANT_DEF = 46;
    int FOR_EACH_CLAUSE = 47;
    int ANNOTATION_DEF = 48;
    int ANNOTATIONS = 49;
    int ANNOTATION = 50;
    int ANNOTATION_MEMBER_VALUE_PAIR = 51;
    int ANNOTATION_FIELD_DEF = 52;
    int ANNOTATION_ARRAY_INIT = 53;
    int TYPE_ARGUMENTS = 54;
    int TYPE_ARGUMENT = 55;
    int TYPE_PARAMETERS = 56;
    int TYPE_PARAMETER = 57;
    int WILDCARD_TYPE = 58;
    int TYPE_UPPER_BOUNDS = 59;
    int TYPE_LOWER_BOUNDS = 60;
    int LITERAL_package = 61;
    int SEMI = 62;
    int LITERAL_import = 63;
    int LITERAL_static = 64;
    int LBRACK = 65;
    int RBRACK = 66;
    int IDENT = 67;
    int DOT = 68;
    int QUESTION = 69;
    int LITERAL_extends = 70;
    int LITERAL_super = 71;
    int LT = 72;
    int GT = 73;
    int COMMA = 74;
    int SR = 75;
    int BSR = 76;
    int LITERAL_void = 77;
    int LITERAL_boolean = 78;
    int LITERAL_byte = 79;
    int LITERAL_char = 80;
    int LITERAL_short = 81;
    int LITERAL_int = 82;
    int LITERAL_float = 83;
    int LITERAL_long = 84;
    int LITERAL_double = 85;
    int STAR = 86;
    int LITERAL_private = 87;
    int LITERAL_public = 88;
    int LITERAL_protected = 89;
    int LITERAL_transient = 90;
    int LITERAL_native = 91;
    int LITERAL_threadsafe = 92;
    int LITERAL_synchronized = 93;
    int LITERAL_volatile = 94;
    int AT = 95;
    int LPAREN = 96;
    int RPAREN = 97;
    int ASSIGN = 98;
    int LCURLY = 99;
    int RCURLY = 100;
    int LITERAL_class = 101;
    int LITERAL_interface = 102;
    int LITERAL_enum = 103;
    int BAND = 104;
    int LITERAL_default = 105;
    int LITERAL_implements = 106;
    int LITERAL_this = 107;
    int LITERAL_throws = 108;
    int TRIPLE_DOT = 109;
    int COLON = 110;
    int LITERAL_if = 111;
    int LITERAL_else = 112;
    int LITERAL_while = 113;
    int LITERAL_do = 114;
    int LITERAL_break = 115;
    int LITERAL_continue = 116;
    int LITERAL_return = 117;
    int LITERAL_switch = 118;
    int LITERAL_throw = 119;
    int LITERAL_assert = 120;
    int LITERAL_for = 121;
    int LITERAL_case = 122;
    int LITERAL_try = 123;
    int LITERAL_finally = 124;
    int LITERAL_catch = 125;
    int BOR = 126;
    int PLUS_ASSIGN = 127;
    int MINUS_ASSIGN = 128;
    int STAR_ASSIGN = 129;
    int DIV_ASSIGN = 130;
    int MOD_ASSIGN = 131;
    int SR_ASSIGN = 132;
    int BSR_ASSIGN = 133;
    int SL_ASSIGN = 134;
    int BAND_ASSIGN = 135;
    int BXOR_ASSIGN = 136;
    int BOR_ASSIGN = 137;
    int LOR = 138;
    int LAND = 139;
    int BXOR = 140;
    int NOT_EQUAL = 141;
    int EQUAL = 142;
    int LE = 143;
    int GE = 144;
    int LITERAL_instanceof = 145;
    int SL = 146;
    int PLUS = 147;
    int MINUS = 148;
    int DIV = 149;
    int MOD = 150;
    int INC = 151;
    int DEC = 152;
    int BNOT = 153;
    int LNOT = 154;
    int LITERAL_true = 155;
    int LITERAL_false = 156;
    int LITERAL_null = 157;
    int LITERAL_new = 158;
    int NUM_INT = 159;
    int CHAR_LITERAL = 160;
    int STRING_LITERAL = 161;
    int NUM_FLOAT = 162;
    int NUM_LONG = 163;
    int NUM_DOUBLE = 164;
    int WS = 165;
    int SL_COMMENT = 166;
    int ML_COMMENT = 167;
    int ESC = 168;
    int HEX_DIGIT = 169;
    int VOCAB = 170;
    int EXPONENT = 171;
    int FLOAT_SUFFIX = 172;
}
