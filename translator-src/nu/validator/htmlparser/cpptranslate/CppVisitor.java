/*
 * Copyright (C) 2007 Júlio Vilmar Gesser.
 * Copyright (C) 2008 Mozilla Foundation
 * 
 * This file is part of HTML Parser C++ Translator. It was derived from DumpVisitor
 * which was part of Java 1.5 parser and Abstract Syntax Tree and came with the following notice:
 *
 * Java 1.5 parser and Abstract Syntax Tree is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Java 1.5 parser and Abstract Syntax Tree is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Java 1.5 parser and Abstract Syntax Tree.  If not, see <http://www.gnu.org/licenses/>.
 */
/*
 * Created on 05/10/2006
 */
package nu.validator.htmlparser.cpptranslate;

import japa.parser.ast.BlockComment;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.LineComment;
import japa.parser.ast.Node;
import japa.parser.ast.PackageDeclaration;
import japa.parser.ast.TypeParameter;
import japa.parser.ast.body.AnnotationDeclaration;
import japa.parser.ast.body.AnnotationMemberDeclaration;
import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.ConstructorDeclaration;
import japa.parser.ast.body.EmptyMemberDeclaration;
import japa.parser.ast.body.EmptyTypeDeclaration;
import japa.parser.ast.body.EnumConstantDeclaration;
import japa.parser.ast.body.EnumDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.InitializerDeclaration;
import japa.parser.ast.body.JavadocComment;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.ModifierSet;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.body.TypeDeclaration;
import japa.parser.ast.body.VariableDeclarator;
import japa.parser.ast.body.VariableDeclaratorId;
import japa.parser.ast.expr.AnnotationExpr;
import japa.parser.ast.expr.ArrayAccessExpr;
import japa.parser.ast.expr.ArrayCreationExpr;
import japa.parser.ast.expr.ArrayInitializerExpr;
import japa.parser.ast.expr.AssignExpr;
import japa.parser.ast.expr.BinaryExpr;
import japa.parser.ast.expr.BooleanLiteralExpr;
import japa.parser.ast.expr.CastExpr;
import japa.parser.ast.expr.CharLiteralExpr;
import japa.parser.ast.expr.ClassExpr;
import japa.parser.ast.expr.ConditionalExpr;
import japa.parser.ast.expr.DoubleLiteralExpr;
import japa.parser.ast.expr.EnclosedExpr;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.FieldAccessExpr;
import japa.parser.ast.expr.InstanceOfExpr;
import japa.parser.ast.expr.IntegerLiteralExpr;
import japa.parser.ast.expr.IntegerLiteralMinValueExpr;
import japa.parser.ast.expr.LongLiteralExpr;
import japa.parser.ast.expr.LongLiteralMinValueExpr;
import japa.parser.ast.expr.MarkerAnnotationExpr;
import japa.parser.ast.expr.MemberValuePair;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.expr.NormalAnnotationExpr;
import japa.parser.ast.expr.NullLiteralExpr;
import japa.parser.ast.expr.ObjectCreationExpr;
import japa.parser.ast.expr.QualifiedNameExpr;
import japa.parser.ast.expr.SingleMemberAnnotationExpr;
import japa.parser.ast.expr.StringLiteralExpr;
import japa.parser.ast.expr.SuperExpr;
import japa.parser.ast.expr.SuperMemberAccessExpr;
import japa.parser.ast.expr.ThisExpr;
import japa.parser.ast.expr.UnaryExpr;
import japa.parser.ast.expr.VariableDeclarationExpr;
import japa.parser.ast.stmt.AssertStmt;
import japa.parser.ast.stmt.BlockStmt;
import japa.parser.ast.stmt.BreakStmt;
import japa.parser.ast.stmt.CatchClause;
import japa.parser.ast.stmt.ContinueStmt;
import japa.parser.ast.stmt.DoStmt;
import japa.parser.ast.stmt.EmptyStmt;
import japa.parser.ast.stmt.ExplicitConstructorInvocationStmt;
import japa.parser.ast.stmt.ExpressionStmt;
import japa.parser.ast.stmt.ForStmt;
import japa.parser.ast.stmt.ForeachStmt;
import japa.parser.ast.stmt.IfStmt;
import japa.parser.ast.stmt.LabeledStmt;
import japa.parser.ast.stmt.ReturnStmt;
import japa.parser.ast.stmt.Statement;
import japa.parser.ast.stmt.SwitchEntryStmt;
import japa.parser.ast.stmt.SwitchStmt;
import japa.parser.ast.stmt.SynchronizedStmt;
import japa.parser.ast.stmt.ThrowStmt;
import japa.parser.ast.stmt.TryStmt;
import japa.parser.ast.stmt.TypeDeclarationStmt;
import japa.parser.ast.stmt.WhileStmt;
import japa.parser.ast.type.ClassOrInterfaceType;
import japa.parser.ast.type.PrimitiveType;
import japa.parser.ast.type.ReferenceType;
import japa.parser.ast.type.Type;
import japa.parser.ast.type.VoidType;
import japa.parser.ast.type.WildcardType;
import japa.parser.ast.visitor.VoidVisitor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author Julio Vilmar Gesser
 * @author Henri Sivonen
 */

public class CppVisitor implements VoidVisitor<Object> {

    private static final String[] CLASS_NAMES = { "AttributeName",
            "ElementName", "HtmlAttributes", "LocatorImpl", "NamedCharacters",
            "Portability", "StackNode", "Tokenizer", "TreeBuilder",
            "UTF16Buffer" };

    public class SourcePrinter {

        private int level = 0;

        private boolean indented = false;

        private final StringBuilder buf = new StringBuilder();

        public void indent() {
            level++;
        }

        public void unindent() {
            level--;
        }

        private void makeIndent() {
            for (int i = 0; i < level; i++) {
                buf.append("  ");
            }
        }

        public void print(String arg) {
            if (!indented) {
                makeIndent();
                indented = true;
            }
            buf.append(arg);
        }

        public void printLn(String arg) {
            print(arg);
            printLn();
        }

        public void printLn() {
            buf.append("\n");
            indented = false;
        }

        public String getSource() {
            return buf.toString();
        }

        @Override public String toString() {
            return getSource();
        }
    }

    protected SourcePrinter printer = new SourcePrinter();

    private SourcePrinter staticInitializerPrinter = new SourcePrinter();

    private SourcePrinter tempPrinterHolder;

    protected final CppTypes cppTypes;

    protected String className = "";

    protected List<AnnotationExpr> currentAnnotations;

    protected int currentArrayCount;

    protected Set<String> forLoopsWithCondition = new HashSet<String>();

    protected boolean inPrimitiveNoLengthFieldDeclarator = false;

    protected final SymbolTable symbolTable;

    protected String definePrefix;

    protected String javaClassName;

    protected boolean suppressPointer = false;

    private final List<String> staticReleases = new LinkedList<String>();

    private boolean inConstructorBody = false;

    private String currentMethod = null;
    
    /**
     * @param cppTypes
     */
    public CppVisitor(CppTypes cppTypes, SymbolTable symbolTable) {
        this.cppTypes = cppTypes;
        this.symbolTable = symbolTable;
        staticInitializerPrinter.indent();
    }

    public String getSource() {
        return printer.getSource();
    }

    private String classNameFromExpression(Expression e) {
        if (e instanceof NameExpr) {
            NameExpr nameExpr = (NameExpr) e;
            String name = nameExpr.getName();
            if (Arrays.binarySearch(CLASS_NAMES, name) > -1) {
                return name;
            }
        }
        return null;
    }

    protected void printModifiers(int modifiers) {
    }

    private void printMembers(List<BodyDeclaration> members, Object arg) {
        for (BodyDeclaration member : members) {
            member.accept(this, arg);
        }
    }

    private void printTypeArgs(List<Type> args, Object arg) {
        // if (args != null) {
        // printer.print("<");
        // for (Iterator<Type> i = args.iterator(); i.hasNext();) {
        // Type t = i.next();
        // t.accept(this, arg);
        // if (i.hasNext()) {
        // printer.print(", ");
        // }
        // }
        // printer.print(">");
        // }
    }

    private void printTypeParameters(List<TypeParameter> args, Object arg) {
        // if (args != null) {
        // printer.print("<");
        // for (Iterator<TypeParameter> i = args.iterator(); i.hasNext();) {
        // TypeParameter t = i.next();
        // t.accept(this, arg);
        // if (i.hasNext()) {
        // printer.print(", ");
        // }
        // }
        // printer.print(">");
        // }
    }

    public void visit(Node n, Object arg) {
        throw new IllegalStateException(n.getClass().getName());
    }

    public void visit(CompilationUnit n, Object arg) {
        if (n.getTypes() != null) {
            for (Iterator<TypeDeclaration> i = n.getTypes().iterator(); i.hasNext();) {
                i.next().accept(this, arg);
                printer.printLn();
                if (i.hasNext()) {
                    printer.printLn();
                }
            }
        }
    }

    public void visit(PackageDeclaration n, Object arg) {
        throw new IllegalStateException(n.getClass().getName());
    }

    public void visit(NameExpr n, Object arg) {
        if ("mappingLangToXmlLang".equals(n.getName())) {
            printer.print("0");
        } else if ("LANG_NS".equals(n.getName())) {
            printer.print("ALL_NO_NS");
        } else if ("LANG_PREFIX".equals(n.getName())) {
            printer.print("ALL_NO_PREFIX");
        } else {
            String prefixedName = javaClassName + "." + n.getName();
            String constant = symbolTable.cppDefinesByJavaNames.get(prefixedName);
            if (constant != null) {
                printer.print(constant);
            } else {
                printer.print(n.getName());
            }
        }
    }

    public void visit(QualifiedNameExpr n, Object arg) {
        n.getQualifier().accept(this, arg);
        printer.print(".");
        printer.print(n.getName());
    }

    public void visit(ImportDeclaration n, Object arg) {
        throw new IllegalStateException(n.getClass().getName());
    }

    public void visit(ClassOrInterfaceDeclaration n, Object arg) {
        javaClassName = n.getName();
        className = cppTypes.classPrefix() + javaClassName;
        definePrefix = makeDefinePrefix(className);

        startClassDeclaration();

        if (n.getMembers() != null) {
            printMembers(n.getMembers(), arg);
        }

        endClassDeclaration();
    }

    private String makeDefinePrefix(String name) {
        StringBuilder sb = new StringBuilder();
        boolean prevWasLowerCase = true;
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (c >= 'a' && c <= 'z') {
                sb.append((char) (c - 0x20));
                prevWasLowerCase = true;
            } else if (c >= 'A' && c <= 'Z') {
                if (prevWasLowerCase) {
                    sb.append('_');
                }
                sb.append(c);
                prevWasLowerCase = false;
            } else if (c >= '0' && c <= '9') {
                sb.append(c);
                prevWasLowerCase = false;
            }
        }
        sb.append('_');
        return sb.toString();
    }

    protected void endClassDeclaration() {
        printer.printLn("void");
        printer.print(className);
        printer.printLn("::initializeStatics()");
        printer.printLn("{");
        printer.print(staticInitializerPrinter.getSource());
        printer.printLn("}");
        printer.printLn();

        printer.printLn("void");
        printer.print(className);
        printer.printLn("::releaseStatics()");
        printer.printLn("{");
        printer.indent();
        for (String del : staticReleases) {
            printer.print(del);
            printer.printLn(";");
        }
        printer.unindent();
        printer.printLn("}");
        printer.printLn();

        if ("TreeBuilder".equals(javaClassName)) {
            printer.printLn();
            printer.print("#include \"");
            printer.print(cppTypes.treeBuiderCppSupplement());
            printer.printLn("\"");
        }
    }

    protected void startClassDeclaration() {
        String[] incs = cppTypes.boilerplateIncludes();
        for (int i = 0; i < incs.length; i++) {
            String inc = incs[i];
            printer.print("#include \"");
            printer.print(inc);
            printer.printLn(".h\"");
        }

        printer.printLn();

        for (int i = 0; i < Main.H_LIST.length; i++) {
            String klazz = Main.H_LIST[i];
            if (!klazz.equals(javaClassName)) {
                printer.print("#include \"");
                printer.print(cppTypes.classPrefix());
                printer.print(klazz);
                printer.printLn(".h\"");
            }
        }
        
        printer.printLn();
        printer.print("#include \"");
        printer.print(className);
        printer.printLn(".h\"");
        printer.printLn();
    }

    public void visit(EmptyTypeDeclaration n, Object arg) {
        if (n.getJavaDoc() != null) {
            n.getJavaDoc().accept(this, arg);
        }
        printer.print(";");
    }

    public void visit(JavadocComment n, Object arg) {
        printer.print("/**");
        printer.print(n.getContent());
        printer.printLn("*/");
    }

    public void visit(ClassOrInterfaceType n, Object arg) {
        if (n.getScope() != null) {
            n.getScope().accept(this, arg);
            printer.print(".");
            throw new IllegalStateException("Can't translate nested classes.");
        }
        String name = n.getName();
        if ("String".equals(name)) {
            if (local()) {
                name = cppTypes.localType();
            } else if (prefix()) {
                name = cppTypes.prefixType();
            } else if (nsUri()) {
                name = cppTypes.nsUriType();
            } else {
                name = cppTypes.stringType();
            }
        } else if ("T".equals(name) || "Object".equals(name)) {
            name = cppTypes.nodeType();
        } else if ("TokenHandler".equals(name)) {
            name = cppTypes.classPrefix() + "TreeBuilder*";
        } else if ("EncodingDeclarationHandler".equals(name)) {
            name = cppTypes.encodingDeclarationHandlerType();
        } else if ("DocumentModeHandler".equals(name)) {
            name = cppTypes.documentModeHandlerType();
        } else if ("DocumentMode".equals(name)) {
            name = cppTypes.documentModeType();
        } else {
            name = cppTypes.classPrefix() + name + (suppressPointer ? "" : "*");
        }
        printer.print(name);
        printTypeArgs(n.getTypeArgs(), arg);
    }

    private boolean nsUri() {
        return hasAnnotation("NsUri");
    }

    private boolean prefix() {
        return hasAnnotation("Prefix");
    }

    private boolean local() {
        return hasAnnotation("Local");
    }

    public void visit(TypeParameter n, Object arg) {
        printer.print(n.getName());
        if (n.getTypeBound() != null) {
            printer.print(" extends ");
            for (Iterator<ClassOrInterfaceType> i = n.getTypeBound().iterator(); i.hasNext();) {
                ClassOrInterfaceType c = i.next();
                c.accept(this, arg);
                if (i.hasNext()) {
                    printer.print(" & ");
                }
            }
        }
    }

    public void visit(PrimitiveType n, Object arg) {
        switch (n.getType()) {
            case Boolean:
                printer.print(cppTypes.booleanType());
                break;
            case Byte:
                throw new IllegalStateException("Unsupported primitive.");
            case Char:
                printer.print(cppTypes.charType());
                break;
            case Double:
                throw new IllegalStateException("Unsupported primitive.");
            case Float:
                throw new IllegalStateException("Unsupported primitive.");
            case Int:
                printer.print(cppTypes.intType());
                break;
            case Long:
                throw new IllegalStateException("Unsupported primitive.");
            case Short:
                throw new IllegalStateException("Unsupported primitive.");
        }
    }

    public void visit(ReferenceType n, Object arg) {
        if (noLength()) {
            n.getType().accept(this, arg);
            for (int i = 0; i < n.getArrayCount(); i++) {
                if (!inPrimitiveNoLengthFieldDeclarator) {
                    printer.print("*");
                }
            }
        } else {
            for (int i = 0; i < n.getArrayCount(); i++) {
                printer.print(cppTypes.arrayTemplate());
                printer.print("<");
            }
            n.getType().accept(this, arg);
            for (int i = 0; i < n.getArrayCount(); i++) {
                printer.print(",");
                printer.print(cppTypes.intType());
                printer.print(">");
            }
        }
    }

    protected boolean noLength() {
        return hasAnnotation("NoLength");
    }

    private boolean hasAnnotation(String anno) {
        if (currentAnnotations == null) {
            return false;
        }
        for (AnnotationExpr ann : currentAnnotations) {
            if (ann instanceof MarkerAnnotationExpr) {
                MarkerAnnotationExpr marker = (MarkerAnnotationExpr) ann;
                if (marker.getName().getName().equals(anno)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void visit(WildcardType n, Object arg) {
        printer.print("?");
        if (n.getExtends() != null) {
            printer.print(" extends ");
            n.getExtends().accept(this, arg);
        }
        if (n.getSuper() != null) {
            printer.print(" super ");
            n.getSuper().accept(this, arg);
        }
    }

    public void visit(FieldDeclaration n, Object arg) {
        currentAnnotations = n.getAnnotations();
        fieldDeclaration(n, arg);
        currentAnnotations = null;
    }

    protected boolean isNonToCharArrayMethodCall(Expression exp) {
        if (exp instanceof MethodCallExpr) {
            MethodCallExpr mce = (MethodCallExpr) exp;
            return !"toCharArray".equals(mce.getName());
        } else {
            return false;
        }
    }

    protected void fieldDeclaration(FieldDeclaration n, Object arg) {
        tempPrinterHolder = printer;
        printer = staticInitializerPrinter;
        int modifiers = n.getModifiers();
        List<VariableDeclarator> variables = n.getVariables();
        VariableDeclarator declarator = variables.get(0);
        if (ModifierSet.isStatic(modifiers) && ModifierSet.isFinal(modifiers)
                && !(n.getType() instanceof PrimitiveType)
                && declarator.getInit() != null) {
            if (n.getType() instanceof ReferenceType) {
                ReferenceType rt = (ReferenceType) n.getType();
                currentArrayCount = rt.getArrayCount();
                if (currentArrayCount > 0) {
                    if (currentArrayCount != 1) {
                        throw new IllegalStateException(
                                "Multidimensional arrays not supported. " + n);
                    }
                    if (noLength()) {
                        if (rt.getType() instanceof PrimitiveType) {
                            // do nothing
                        } else {
                            staticReleases.add("delete[] "
                                    + declarator.getId().getName());

                            ArrayInitializerExpr aie = (ArrayInitializerExpr) declarator.getInit();

                            declarator.getId().accept(this, arg);
                            printer.print(" = new ");
                            // suppressPointer = true;
                            rt.getType().accept(this, arg);
                            // suppressPointer = false;
                            printer.print("[");
                            printer.print("" + aie.getValues().size());
                            printer.printLn("];");

                            printArrayInit(declarator.getId(), aie.getValues(),
                                    arg);
                        }
                    } else if (isNonToCharArrayMethodCall(declarator.getInit())
                            || !(rt.getType() instanceof PrimitiveType)) {
                        staticReleases.add(declarator.getId().getName()
                                + ".release()");
                        declarator.getId().accept(this, arg);
                        printer.print(" = ");
                        if (declarator.getInit() instanceof ArrayInitializerExpr) {

                            ArrayInitializerExpr aie = (ArrayInitializerExpr) declarator.getInit();
                            printer.print(cppTypes.arrayTemplate());
                            printer.print("<");
                            suppressPointer = true;
                            rt.getType().accept(this, arg);
                            suppressPointer = false;
                            printer.print(",");
                            printer.print(cppTypes.intType());
                            printer.print(">(");
                            printer.print("" + aie.getValues().size());
                            printer.printLn(");");
                            printArrayInit(declarator.getId(), aie.getValues(),
                                    arg);
                        } else {
                            declarator.getInit().accept(this, arg);
                            printer.printLn(";");
                        }
                    }
                } else {
                    staticReleases.add("delete " + declarator.getId().getName());

                    declarator.accept(this, arg);
                    printer.printLn(";");
                }
            } else {
                throw new IllegalStateException(
                        "Non-reference, non-primitive fields not supported.");
            }
        }
        currentArrayCount = 0;
        printer = tempPrinterHolder;
    }

    private void printArrayInit(VariableDeclaratorId variableDeclaratorId,
            List<Expression> values, Object arg) {
        for (int i = 0; i < values.size(); i++) {
            Expression exp = values.get(i);
            variableDeclaratorId.accept(this, arg);
            printer.print("[");
            printer.print("" + i);
            printer.print("] = ");
            exp.accept(this, arg);
            printer.printLn(";");
        }
    }

    public void visit(VariableDeclarator n, Object arg) {
        n.getId().accept(this, arg);

        if (n.getInit() != null) {
            printer.print(" = ");
            n.getInit().accept(this, arg);
        }
    }

    public void visit(VariableDeclaratorId n, Object arg) {
        printer.print(n.getName());
        if (noLength()) {
            for (int i = 0; i < currentArrayCount; i++) {
                if (inPrimitiveNoLengthFieldDeclarator) {
                    printer.print("[]");
                }
            }
        }
        for (int i = 0; i < n.getArrayCount(); i++) {
            printer.print("[]");
        }
    }

    public void visit(ArrayInitializerExpr n, Object arg) {
        printer.print("{");
        if (n.getValues() != null) {
            printer.print(" ");
            for (Iterator<Expression> i = n.getValues().iterator(); i.hasNext();) {
                Expression expr = i.next();
                expr.accept(this, arg);
                if (i.hasNext()) {
                    printer.print(", ");
                }
            }
            printer.print(" ");
        }
        printer.print("}");
    }

    public void visit(VoidType n, Object arg) {
        printer.print("void");
    }

    public void visit(ArrayAccessExpr n, Object arg) {
        n.getName().accept(this, arg);
        printer.print("[");
        n.getIndex().accept(this, arg);
        printer.print("]");
    }

    public void visit(ArrayCreationExpr n, Object arg) {
        // printer.print("new ");
        // n.getType().accept(this, arg);
        // printTypeArgs(n.getTypeArgs(), arg);

        if (n.getDimensions() != null) {
            if (noLength()) {
                for (Expression dim : n.getDimensions()) {
                    printer.print("new ");
                    n.getType().accept(this, arg);
                    printer.print("[");
                    dim.accept(this, arg);
                    printer.print("]");
                }
            } else {
                for (Expression dim : n.getDimensions()) {
                    printer.print(cppTypes.arrayTemplate());
                    printer.print("<");
                    n.getType().accept(this, arg);
                    printer.print(",");
                    printer.print(cppTypes.intType());
                    printer.print(">(");
                    dim.accept(this, arg);
                    printer.print(")");
                }
            }
            if (n.getArrayCount() > 0) {
                throw new IllegalStateException(
                        "Nested array allocation not supported. "
                                + n.toString());
            }
        } else {
            throw new IllegalStateException(
                    "Array initializer as part of array creation not supported. "
                            + n.toString());
        }
    }

    public void visit(AssignExpr n, Object arg) {
        if (inConstructorBody) {
            n.getTarget().accept(this, arg);
            printer.print("(");
            n.getValue().accept(this, arg);
            printer.print(")");
        } else {
            n.getTarget().accept(this, arg);
            printer.print(" ");
            switch (n.getOperator()) {
                case assign:
                    printer.print("=");
                    break;
                case and:
                    printer.print("&=");
                    break;
                case or:
                    printer.print("|=");
                    break;
                case xor:
                    printer.print("^=");
                    break;
                case plus:
                    printer.print("+=");
                    break;
                case minus:
                    printer.print("-=");
                    break;
                case rem:
                    printer.print("%=");
                    break;
                case slash:
                    printer.print("/=");
                    break;
                case star:
                    printer.print("*=");
                    break;
                case lShift:
                    printer.print("<<=");
                    break;
                case rSignedShift:
                    printer.print(">>=");
                    break;
                case rUnsignedShift:
                    printer.print(">>>=");
                    break;
            }
            printer.print(" ");
            n.getValue().accept(this, arg);
        }
    }

    public void visit(BinaryExpr n, Object arg) {
        Expression right = n.getRight();
        switch (n.getOperator()) {
            case notEquals:
                if (right instanceof NullLiteralExpr) {
                    printer.print("!!");
                    n.getLeft().accept(this, arg);
                    return;
                } else if (right instanceof IntegerLiteralExpr) {
                    IntegerLiteralExpr ile = (IntegerLiteralExpr) right;
                    if ("0".equals(ile.getValue())) {
                        n.getLeft().accept(this, arg);
                        return;
                    }
                }
            case equals:
                if (right instanceof NullLiteralExpr) {
                    printer.print("!");
                    n.getLeft().accept(this, arg);
                    return;
                } else if (right instanceof IntegerLiteralExpr) {
                    IntegerLiteralExpr ile = (IntegerLiteralExpr) right;
                    if ("0".equals(ile.getValue())) {
                        printer.print("!");
                        n.getLeft().accept(this, arg);
                        return;
                    }
                }
            default:
                // fall thru
        }

        n.getLeft().accept(this, arg);
        printer.print(" ");
        switch (n.getOperator()) {
            case or:
                printer.print("||");
                break;
            case and:
                printer.print("&&");
                break;
            case binOr:
                printer.print("|");
                break;
            case binAnd:
                printer.print("&");
                break;
            case xor:
                printer.print("^");
                break;
            case equals:
                printer.print("==");
                break;
            case notEquals:
                printer.print("!=");
                break;
            case less:
                printer.print("<");
                break;
            case greater:
                printer.print(">");
                break;
            case lessEquals:
                printer.print("<=");
                break;
            case greaterEquals:
                printer.print(">=");
                break;
            case lShift:
                printer.print("<<");
                break;
            case rSignedShift:
                printer.print(">>");
                break;
            case rUnsignedShift:
                printer.print(">>>");
                break;
            case plus:
                printer.print("+");
                break;
            case minus:
                printer.print("-");
                break;
            case times:
                printer.print("*");
                break;
            case divide:
                printer.print("/");
                break;
            case remainder:
                printer.print("%");
                break;
        }
        printer.print(" ");
        n.getRight().accept(this, arg);
    }

    public void visit(CastExpr n, Object arg) {
        printer.print("(");
        n.getType().accept(this, arg);
        printer.print(") ");
        n.getExpr().accept(this, arg);
    }

    public void visit(ClassExpr n, Object arg) {
        n.getType().accept(this, arg);
        printer.print(".class");
    }

    public void visit(ConditionalExpr n, Object arg) {
        n.getCondition().accept(this, arg);
        printer.print(" ? ");
        n.getThenExpr().accept(this, arg);
        printer.print(" : ");
        n.getElseExpr().accept(this, arg);
    }

    public void visit(EnclosedExpr n, Object arg) {
        printer.print("(");
        n.getInner().accept(this, arg);
        printer.print(")");
    }

    public void visit(FieldAccessExpr n, Object arg) {
        Expression scope = n.getScope();
        String field = n.getField();
        if (inConstructorBody && (scope instanceof ThisExpr)) {
            printer.print(field);
        } else if ("length".equals(field) && !(scope instanceof ThisExpr)) {
            scope.accept(this, arg);
            printer.print(".length");
        } else if ("MAX_VALUE".equals(field) && "Integer".equals(scope.toString())) {
            printer.print(cppTypes.maxInteger());
        } else {
            String clazzName = classNameFromExpression(scope);
            if (clazzName == null) {
                if ("DocumentMode".equals(scope.toString())) {
//                    printer.print(cppTypes.documentModeType());
//                    printer.print(".");
                } else {
                    scope.accept(this, arg);
                    printer.print("->");
                }
            } else {
                String prefixedName = clazzName + "." + field;
                String constant = symbolTable.cppDefinesByJavaNames.get(prefixedName);
                if (constant != null) {
                    printer.print(constant);
                    return;
                } else {
                    printer.print(cppTypes.classPrefix());
                    printer.print(clazzName);
                    printer.print("::");
                }
            }
            printer.print(field);
        }
    }

    public void visit(InstanceOfExpr n, Object arg) {
        n.getExpr().accept(this, arg);
        printer.print(" instanceof ");
        n.getType().accept(this, arg);
    }

    public void visit(CharLiteralExpr n, Object arg) {
        printCharLiteral(n.getValue());
    }

    private void printCharLiteral(String val) {
        if (val.length() != 1) {
            printer.print("'");
            printer.print(val);
            printer.print("'");
            return;
        }
        char c = val.charAt(0);
        switch (c) {
            case 0:
                printer.print("'\\0'");
                break;
            case '\n':
                printer.print("'\\n'");
                break;
            case '\t':
                printer.print("'\\t'");
                break;
            case 0xB:
                printer.print("'\\v'");
                break;
            case '\b':
                printer.print("'\\b'");
                break;
            case '\r':
                printer.print("'\\r'");
                break;
            case 0xC:
                printer.print("'\\f'");
                break;
            case 0x7:
                printer.print("'\\a'");
                break;
            case '\\':
                printer.print("'\\\\'");
                break;
            case '?':
                printer.print("'\\?'");
                break;
            case '\'':
                printer.print("'\\''");
                break;
            case '"':
                printer.print("'\\\"'");
                break;
            default:
                if (c >= 0x20 && c <= 0x7F) {
                    printer.print("'" + c);
                    printer.print("'");
                } else {
                    printer.print("0x");
                    printer.print(Integer.toHexString(c));
                }
                break;
        }
    }

    public void visit(DoubleLiteralExpr n, Object arg) {
        printer.print(n.getValue());
    }

    public void visit(IntegerLiteralExpr n, Object arg) {
        printer.print(n.getValue());
    }

    public void visit(LongLiteralExpr n, Object arg) {
        printer.print(n.getValue());
    }

    public void visit(IntegerLiteralMinValueExpr n, Object arg) {
        printer.print(n.getValue());
    }

    public void visit(LongLiteralMinValueExpr n, Object arg) {
        printer.print(n.getValue());
    }

    public void visit(StringLiteralExpr n, Object arg) {
        String val = n.getValue();
        if ("http://www.w3.org/1999/xhtml".equals(val)) {
            printer.print(cppTypes.xhtmlNamespaceLiteral());
        } else if ("http://www.w3.org/2000/svg".equals(val)) {
            printer.print(cppTypes.svgNamespaceLiteral());
        } else if ("http://www.w3.org/2000/xmlns/".equals(val)) {
            printer.print(cppTypes.xmlnsNamespaceLiteral());
        } else if ("http://www.w3.org/XML/1998/namespace".equals(val)) {
            printer.print(cppTypes.xmlNamespaceLiteral());
        } else if ("http://www.w3.org/1999/xlink".equals(val)) {
            printer.print(cppTypes.xlinkNamespaceLiteral());
        } else if ("http://www.w3.org/1998/Math/MathML".equals(val)) {
            printer.print(cppTypes.mathmlNamespaceLiteral());
        } else if ("".equals(val) && "AttributeName".equals(javaClassName)) {
            printer.print(cppTypes.noNamespaceLiteral());
        } else if (val.startsWith("-/") || val.startsWith("+//")
                || val.startsWith("http://") || val.startsWith("XSLT")) {
            printer.print(cppTypes.stringForLiteral(val));
        } else if (("hidden".equals(val) || "isindex".equals(val)) && "TreeBuilder".equals(javaClassName)) {
            printer.print(cppTypes.stringForLiteral(val));
        } else if ("isQuirky".equals(currentMethod) && "html".equals(val)) {
            printer.print(cppTypes.stringForLiteral(val));            
        } else {
            printer.print(cppTypes.localForLiteral(val));
        }
    }

    public void visit(BooleanLiteralExpr n, Object arg) {
        if ("true".equals(n.getValue().toString())) {
            printer.print(cppTypes.trueLiteral());
        } else {
            printer.print(cppTypes.falseLiteral());
        }
    }

    public void visit(NullLiteralExpr n, Object arg) {
        printer.print(cppTypes.nullLiteral());
    }

    public void visit(ThisExpr n, Object arg) {
        if (n.getClassExpr() != null) {
            n.getClassExpr().accept(this, arg);
            printer.print(".");
        }
        printer.print("this");
    }

    public void visit(SuperExpr n, Object arg) {
        if (n.getClassExpr() != null) {
            n.getClassExpr().accept(this, arg);
            printer.print(".");
        }
        printer.print("super");
    }

    public void visit(MethodCallExpr n, Object arg) {
        if ("releaseArray".equals(n.getName())
                && "Portability".equals(n.getScope().toString())) {
            n.getArgs().get(0).accept(this, arg);
            printer.print(".release()");
        } else if ("deleteArray".equals(n.getName())
                && "Portability".equals(n.getScope().toString())) {
            printer.print("delete[] ");
            n.getArgs().get(0).accept(this, arg);
        } else if ("delete".equals(n.getName())
                && "Portability".equals(n.getScope().toString())) {
            printer.print("delete ");
            n.getArgs().get(0).accept(this, arg);
        } else if ("arraycopy".equals(n.getName())
                && "System".equals(n.getScope().toString())) {
            printer.print(cppTypes.arrayCopy());
            printer.print("(");
            if (n.getArgs().get(0).toString().equals(n.getArgs().get(2).toString())) {
                n.getArgs().get(0).accept(this, arg);
                printer.print(", ");
                n.getArgs().get(1).accept(this, arg);
                printer.print(", ");
                n.getArgs().get(3).accept(this, arg);                
                printer.print(", ");
                n.getArgs().get(4).accept(this, arg);                
            } else if (n.getArgs().get(1).toString().equals("0") && n.getArgs().get(3).toString().equals("0")) {
                n.getArgs().get(0).accept(this, arg);
                printer.print(", ");
                n.getArgs().get(2).accept(this, arg);
                printer.print(", ");
                n.getArgs().get(4).accept(this, arg);
            } else {
                for (Iterator<Expression> i = n.getArgs().iterator(); i.hasNext();) {
                    Expression e = i.next();
                    e.accept(this, arg);
                    if (i.hasNext()) {
                        printer.print(", ");
                    }
                }
            }
            printer.print(")");
        } else if ("binarySearch".equals(n.getName())
                && "Arrays".equals(n.getScope().toString())) {
            n.getArgs().get(0).accept(this, arg);
            printer.print(".binarySearch(");
            n.getArgs().get(1).accept(this, arg);
            printer.print(")");
        } else {
            Expression scope = n.getScope();
            if (scope != null) {
                if (scope instanceof StringLiteralExpr) {
                    StringLiteralExpr strLit = (StringLiteralExpr) scope;
                    String str = strLit.getValue();
                    if (!"toCharArray".equals(n.getName())) {
                        throw new IllegalStateException(
                                "Unsupported method call on string literal: "
                                        + n.getName());
                    }
                    printer.print("{ ");
                    for (int i = 0; i < str.length(); i++) {
                        char c = str.charAt(i);
                        if (i != 0) {
                            printer.print(", ");
                        }
                        printCharLiteral("" + c);
                    }
                    printer.print(" }");
                    return;
                } else {
                    String clazzName = classNameFromExpression(scope);
                    if (clazzName == null) {
                        scope.accept(this, arg);
                        printer.print("->");
                    } else {
                        printer.print(cppTypes.classPrefix());
                        printer.print(clazzName);
                        printer.print("::");
                    }
                }
            }
            printTypeArgs(n.getTypeArgs(), arg);
            printer.print(n.getName());
            printer.print("(");
            if (n.getArgs() != null) {
                for (Iterator<Expression> i = n.getArgs().iterator(); i.hasNext();) {
                    Expression e = i.next();
                    e.accept(this, arg);
                    if (i.hasNext()) {
                        printer.print(", ");
                    }
                }
            }
            printer.print(")");
        }
    }

    public void visit(ObjectCreationExpr n, Object arg) {
        if (n.getScope() != null) {
            n.getScope().accept(this, arg);
            printer.print(".");
        }

        printer.print("new ");

        suppressPointer = true;
        printTypeArgs(n.getTypeArgs(), arg);
        n.getType().accept(this, arg);
        suppressPointer = false;

        if ("AttributeName".equals(n.getType().getName())) {
            List<Expression> args = n.getArgs();
            while (args.size() > 3) {
                args.remove(3);
            }
        }

        printer.print("(");
        if (n.getArgs() != null) {
            for (Iterator<Expression> i = n.getArgs().iterator(); i.hasNext();) {
                Expression e = i.next();
                e.accept(this, arg);
                if (i.hasNext()) {
                    printer.print(", ");
                }
            }
        }
        printer.print(")");

        if (n.getAnonymousClassBody() != null) {
            printer.printLn(" {");
            printer.indent();
            printMembers(n.getAnonymousClassBody(), arg);
            printer.unindent();
            printer.print("}");
        }
    }

    public void visit(SuperMemberAccessExpr n, Object arg) {
        printer.print("super.");
        printer.print(n.getName());
    }

    public void visit(UnaryExpr n, Object arg) {
        switch (n.getOperator()) {
            case positive:
                printer.print("+");
                break;
            case negative:
                printer.print("-");
                break;
            case inverse:
                printer.print("~");
                break;
            case not:
                printer.print("!");
                break;
            case preIncrement:
                printer.print("++");
                break;
            case preDecrement:
                printer.print("--");
                break;
        }

        n.getExpr().accept(this, arg);

        switch (n.getOperator()) {
            case posIncrement:
                printer.print("++");
                break;
            case posDecrement:
                printer.print("--");
                break;
        }
    }

    public void visit(ConstructorDeclaration n, Object arg) {
        if ("TreeBuilder".equals(javaClassName)) {
            return;
        }

        // if (n.getJavaDoc() != null) {
        // n.getJavaDoc().accept(this, arg);
        // }
        currentAnnotations = n.getAnnotations();
        // if (annotations != null) {
        // for (AnnotationExpr a : annotations) {
        // a.accept(this, arg);
        // printer.printLn();
        // }
        // }
        printModifiers(n.getModifiers());

        // printTypeParameters(n.getTypeParameters(), arg);
        // if (n.getTypeParameters() != null) {
        // printer.print(" ");
        // }

        printMethodNamespace();
        printer.print(className);
        currentAnnotations = null;

        printer.print("(");
        if (n.getParameters() != null) {
            for (Iterator<Parameter> i = n.getParameters().iterator(); i.hasNext();) {
                Parameter p = i.next();
                p.accept(this, arg);
                if (i.hasNext()) {
                    printer.print(", ");
                }
            }
        }
        printer.print(")");

        // if (n.getThrows() != null) {
        // printer.print(" throws ");
        // for (Iterator<NameExpr> i = n.getThrows().iterator(); i.hasNext();) {
        // NameExpr name = i.next();
        // name.accept(this, arg);
        // if (i.hasNext()) {
        // printer.print(", ");
        // }
        // }
        // }
        printConstructorBody(n.getBlock(), arg);
    }

    protected void printConstructorBody(BlockStmt block, Object arg) {
        inConstructorBody = true;
        List<Statement> statements = block.getStmts();
        int i = 0;
        boolean needOutdent = false;
        for (Statement statement : statements) {
            if (i == 0) {
                printer.printLn();
                printer.indent();
                printer.print(": ");
                needOutdent = true;
            } else {
                printer.print(",");
                printer.printLn();
                printer.print("  ");
            }
            statement.accept(this, arg);
            i++;
        }
        if (needOutdent) {
            printer.unindent();
        }
        inConstructorBody = false;
        printer.printLn();
        printer.printLn("{");
        printer.printLn("}");
        printer.printLn();
    }

    public void visit(MethodDeclaration n, Object arg) {
        if (isPrintableMethod(n.getModifiers())) {
            printMethodDeclaration(n, arg);
        }
    }

    private boolean isPrintableMethod(int modifiers) {
        return !(ModifierSet.isAbstract(modifiers) || (ModifierSet.isProtected(modifiers) && !ModifierSet.isFinal(modifiers)));
    }

    protected void printMethodDeclaration(MethodDeclaration n, Object arg) {
        if (n.getName().startsWith("fatal") || n.getName().startsWith("err")
                || n.getName().startsWith("warn")
                || "releaseArray".equals(n.getName())
                || "deleteArray".equals(n.getName())
                || "delete".equals(n.getName())) {
            return;
        }
        
        currentMethod = n.getName();

        // if (n.getJavaDoc() != null) {
        // n.getJavaDoc().accept(this, arg);
        // }
        currentAnnotations = n.getAnnotations();
        printModifiers(n.getModifiers());

        printTypeParameters(n.getTypeParameters(), arg);
        if (n.getTypeParameters() != null) {
            printer.print(" ");
        }

        n.getType().accept(this, arg);
        printer.print(" ");
        printMethodNamespace();
        printer.print(n.getName());

        currentAnnotations = null;
        printer.print("(");
        if (n.getParameters() != null) {
            for (Iterator<Parameter> i = n.getParameters().iterator(); i.hasNext();) {
                Parameter p = i.next();
                p.accept(this, arg);
                if (i.hasNext()) {
                    printer.print(", ");
                }
            }
        }
        printer.print(")");

        for (int i = 0; i < n.getArrayCount(); i++) {
            printer.print("[]");
        }

        printMethodBody(n.getBody(), arg);
    }

    protected void printMethodBody(BlockStmt n, Object arg) {
        if (n == null) {
            printer.print(";");
        } else {
            printer.printLn();
            n.accept(this, arg);
        }
        printer.printLn();
        printer.printLn();
    }

    protected void printMethodNamespace() {
        printer.printLn();
        printer.print(className);
        printer.print("::");
    }

    public void visit(Parameter n, Object arg) {
        currentAnnotations = n.getAnnotations();
        // if (annotations != null) {
        // for (AnnotationExpr a : annotations) {
        // a.accept(this, arg);
        // printer.print(" ");
        // }
        // }
        // printModifiers(n.getModifiers());

        n.getType().accept(this, arg);
        if (n.isVarArgs()) {
            printer.print("...");
        }
        printer.print(" ");
        n.getId().accept(this, arg);
        currentAnnotations = null;
    }

    public void visit(ExplicitConstructorInvocationStmt n, Object arg) {
        if (n.isThis()) {
            printTypeArgs(n.getTypeArgs(), arg);
            printer.print("this");
        } else {
            if (n.getExpr() != null) {
                n.getExpr().accept(this, arg);
                printer.print(".");
            }
            printTypeArgs(n.getTypeArgs(), arg);
            printer.print("super");
        }
        printer.print("(");
        if (n.getArgs() != null) {
            for (Iterator<Expression> i = n.getArgs().iterator(); i.hasNext();) {
                Expression e = i.next();
                e.accept(this, arg);
                if (i.hasNext()) {
                    printer.print(", ");
                }
            }
        }
        printer.print(");");
    }

    public void visit(VariableDeclarationExpr n, Object arg) {
        currentAnnotations = n.getAnnotations();
        // if (annotations != null) {
        // for (AnnotationExpr a : annotations) {
        // a.accept(this, arg);
        // printer.print(" ");
        // }
        // }
        printModifiers(n.getModifiers());

        n.getType().accept(this, arg);
        printer.print(" ");

        for (Iterator<VariableDeclarator> i = n.getVars().iterator(); i.hasNext();) {
            VariableDeclarator v = i.next();
            v.accept(this, arg);
            if (i.hasNext()) {
                printer.print(", ");
            }
        }
        currentAnnotations = null;
    }

    public void visit(TypeDeclarationStmt n, Object arg) {
        n.getTypeDeclaration().accept(this, arg);
    }

    public void visit(AssertStmt n, Object arg) {
    }

    public void visit(BlockStmt n, Object arg) {
        printer.printLn("{");
        if (n.getStmts() != null) {
            printer.indent();
            for (Statement s : n.getStmts()) {
                s.accept(this, arg);
                printer.printLn();
            }
            printer.unindent();
        }
        printer.print("}");

    }

    public void visit(LabeledStmt n, Object arg) {
        // Only conditionless for loops are needed and supported
        // Not implementing general Java continue semantics in order
        // to keep the generated C++ more readable.
        Statement stmt = n.getStmt();
        if (stmt instanceof ForStmt) {
            ForStmt forLoop = (ForStmt) stmt;
            if (!(forLoop.getInit() == null && forLoop.getCompare() == null && forLoop.getUpdate() == null)) {
                forLoopsWithCondition.add(n.getLabel());
            }
        } else {
            throw new IllegalStateException(
                    "Only for loop supported as labeled statement. Line: "
                            + n.getBeginLine());
        }
        printer.print(n.getLabel());
        printer.print(": ");
        stmt.accept(this, arg);
        printer.printLn();
        printer.print(n.getLabel());
        printer.print("_end");
        printer.print(": ;");
    }

    public void visit(EmptyStmt n, Object arg) {
        printer.print(";");
    }

    public void visit(ExpressionStmt n, Object arg) {
        Expression e = n.getExpression();
        if(isDroppedExpression(e)) {
            return;
        }
        e.accept(this, arg);
        if (!inConstructorBody) {
            printer.print(";");
        }
    }

    private boolean isDroppedExpression(Expression e) {
        if (e instanceof MethodCallExpr) {
            MethodCallExpr methodCallExpr = (MethodCallExpr) e;
            String name = methodCallExpr.getName();
            if (name.startsWith("fatal") || name.startsWith("err")
                    || name.startsWith("warn")) {
                return true;
            }
        }
        return false;
    }

    public void visit(SwitchStmt n, Object arg) {
        printer.print("switch(");
        n.getSelector().accept(this, arg);
        printer.printLn(") {");
        if (n.getEntries() != null) {
            printer.indent();
            for (SwitchEntryStmt e : n.getEntries()) {
                e.accept(this, arg);
            }
            printer.unindent();
        }
        printer.print("}");

    }

    public void visit(SwitchEntryStmt n, Object arg) {
        if (n.getLabel() != null) {
            printer.print("case ");
            n.getLabel().accept(this, arg);
            printer.print(":");
        } else {
            printer.print("default:");
        }
        printer.printLn();
        printer.indent();
        if (isNoStatement(n.getStmts())) {
            if (n.getLabel() == null) {
                printer.printLn("; // fall through");            
            }
        } else {
            for (Statement s : n.getStmts()) {
                s.accept(this, arg);
                printer.printLn();
            }
        }
        printer.unindent();
    }

    private boolean isNoStatement(List<Statement> stmts) {
        if (stmts == null) {
            return true;
        }
        for (Statement statement : stmts) {
            if (!isDroppableStatement(statement)) {
                return false;
            }
        }
        return true;
    }

    private boolean isDroppableStatement(Statement statement) {
        if (statement instanceof AssertStmt) {
            return true;
        } else if (statement instanceof ExpressionStmt) {
            ExpressionStmt es = (ExpressionStmt) statement;
            if(isDroppedExpression(es.getExpression())) {
                return true;
            }
        }
        return false;
    }

    public void visit(BreakStmt n, Object arg) {
        if (n.getId() != null) {
            printer.print("goto ");
            printer.print(n.getId());
            printer.print("_end");
        } else {
            printer.print("break");
        }
        printer.print(";");
    }

    public void visit(ReturnStmt n, Object arg) {
        printer.print("return");
        if (n.getExpr() != null) {
            printer.print(" ");
            n.getExpr().accept(this, arg);
        }
        printer.print(";");
    }

    public void visit(EnumDeclaration n, Object arg) {
        if (n.getJavaDoc() != null) {
            n.getJavaDoc().accept(this, arg);
        }
        currentAnnotations = n.getAnnotations();
        // if (annotations != null) {
        // for (AnnotationExpr a : annotations) {
        // a.accept(this, arg);
        // printer.printLn();
        // }
        // }
        printModifiers(n.getModifiers());

        printer.print("enum ");
        printer.print(n.getName());

        currentAnnotations = null;

        if (n.getImplements() != null) {
            printer.print(" implements ");
            for (Iterator<ClassOrInterfaceType> i = n.getImplements().iterator(); i.hasNext();) {
                ClassOrInterfaceType c = i.next();
                c.accept(this, arg);
                if (i.hasNext()) {
                    printer.print(", ");
                }
            }
        }

        printer.printLn(" {");
        printer.indent();
        if (n.getEntries() != null) {
            printer.printLn();
            for (Iterator<EnumConstantDeclaration> i = n.getEntries().iterator(); i.hasNext();) {
                EnumConstantDeclaration e = i.next();
                e.accept(this, arg);
                if (i.hasNext()) {
                    printer.print(", ");
                }
            }
        }
        if (n.getMembers() != null) {
            printer.printLn(";");
            printMembers(n.getMembers(), arg);
        } else {
            if (n.getEntries() != null) {
                printer.printLn();
            }
        }
        printer.unindent();
        printer.print("}");
    }

    public void visit(EnumConstantDeclaration n, Object arg) {
        if (n.getJavaDoc() != null) {
            n.getJavaDoc().accept(this, arg);
        }
        currentAnnotations = n.getAnnotations();
        // if (annotations != null) {
        // for (AnnotationExpr a : annotations) {
        // a.accept(this, arg);
        // printer.printLn();
        // }
        // }
        printer.print(n.getName());

        currentAnnotations = null;

        if (n.getArgs() != null) {
            printer.print("(");
            for (Iterator<Expression> i = n.getArgs().iterator(); i.hasNext();) {
                Expression e = i.next();
                e.accept(this, arg);
                if (i.hasNext()) {
                    printer.print(", ");
                }
            }
            printer.print(")");
        }

        if (n.getClassBody() != null) {
            printer.printLn(" {");
            printer.indent();
            printMembers(n.getClassBody(), arg);
            printer.unindent();
            printer.printLn("}");
        }
    }

    public void visit(EmptyMemberDeclaration n, Object arg) {
        if (n.getJavaDoc() != null) {
            n.getJavaDoc().accept(this, arg);
        }
        printer.print(";");
    }

    public void visit(InitializerDeclaration n, Object arg) {
        if (n.getJavaDoc() != null) {
            n.getJavaDoc().accept(this, arg);
        }
        if (n.isStatic()) {
            printer.print("static ");
        }
        n.getBlock().accept(this, arg);
    }

    public void visit(IfStmt n, Object arg) {
        if (!isErrorHandlerIf(n.getCondition())) {
            printer.print("if (");
            n.getCondition().accept(this, arg);
            printer.print(") ");
            n.getThenStmt().accept(this, arg);
            if (n.getElseStmt() != null) {
                printer.print(" else ");
                n.getElseStmt().accept(this, arg);
            }            
        }
    }

    private boolean isErrorHandlerIf(Expression condition) {
        if (condition instanceof BinaryExpr) {
            BinaryExpr be = (BinaryExpr) condition;
            return be.getLeft().toString().equals("errorHandler");       
        }
        return false;
    }

    public void visit(WhileStmt n, Object arg) {
        printer.print("while (");
        n.getCondition().accept(this, arg);
        printer.print(") ");
        n.getBody().accept(this, arg);
    }

    public void visit(ContinueStmt n, Object arg) {
        // Not supporting the general Java continue semantics.
        // Instead, making the generated code more readable for the
        // case at hand.
        if (n.getId() != null) {
            printer.print("goto ");
            printer.print(n.getId());
            if (forLoopsWithCondition.contains(n.getId())) {
                throw new IllegalStateException(
                        "Continue attempted with a loop that has a condition. "
                                + className + " " + n.getId());
            }
        } else {
            printer.print("continue");
        }
        printer.print(";");
    }

    public void visit(DoStmt n, Object arg) {
        printer.print("do ");
        n.getBody().accept(this, arg);
        printer.print(" while (");
        n.getCondition().accept(this, arg);
        printer.print(");");
    }

    public void visit(ForeachStmt n, Object arg) {
        printer.print("for (");
        n.getVariable().accept(this, arg);
        printer.print(" : ");
        n.getIterable().accept(this, arg);
        printer.print(") ");
        n.getBody().accept(this, arg);
    }

    public void visit(ForStmt n, Object arg) {
        printer.print("for (");
        if (n.getInit() != null) {
            for (Iterator<Expression> i = n.getInit().iterator(); i.hasNext();) {
                Expression e = i.next();
                e.accept(this, arg);
                if (i.hasNext()) {
                    printer.print(", ");
                }
            }
        }
        printer.print("; ");
        if (n.getCompare() != null) {
            n.getCompare().accept(this, arg);
        }
        printer.print("; ");
        if (n.getUpdate() != null) {
            for (Iterator<Expression> i = n.getUpdate().iterator(); i.hasNext();) {
                Expression e = i.next();
                e.accept(this, arg);
                if (i.hasNext()) {
                    printer.print(", ");
                }
            }
        }
        printer.print(") ");
        n.getBody().accept(this, arg);
    }

    public void visit(ThrowStmt n, Object arg) {
        printer.print("throw ");
        n.getExpr().accept(this, arg);
        printer.print(";");
    }

    public void visit(SynchronizedStmt n, Object arg) {
        printer.print("synchronized (");
        n.getExpr().accept(this, arg);
        printer.print(") ");
        n.getBlock().accept(this, arg);
    }

    public void visit(TryStmt n, Object arg) {
        printer.print("try ");
        n.getTryBlock().accept(this, arg);
        if (n.getCatchs() != null) {
            for (CatchClause c : n.getCatchs()) {
                c.accept(this, arg);
            }
        }
        if (n.getFinallyBlock() != null) {
            printer.print(" finally ");
            n.getFinallyBlock().accept(this, arg);
        }
    }

    public void visit(CatchClause n, Object arg) {
        printer.print(" catch (");
        n.getExcept().accept(this, arg);
        printer.print(") ");
        n.getCatchBlock().accept(this, arg);

    }

    public void visit(AnnotationDeclaration n, Object arg) {
        if (n.getJavaDoc() != null) {
            n.getJavaDoc().accept(this, arg);
        }
        currentAnnotations = n.getAnnotations();
        // if (annotations != null) {
        // for (AnnotationExpr a : annotations) {
        // a.accept(this, arg);
        // printer.printLn();
        // }
        // }
        printModifiers(n.getModifiers());

        printer.print("@interface ");
        printer.print(n.getName());
        currentAnnotations = null;
        printer.printLn(" {");
        printer.indent();
        if (n.getMembers() != null) {
            printMembers(n.getMembers(), arg);
        }
        printer.unindent();
        printer.print("}");
    }

    public void visit(AnnotationMemberDeclaration n, Object arg) {
        if (n.getJavaDoc() != null) {
            n.getJavaDoc().accept(this, arg);
        }
        currentAnnotations = n.getAnnotations();
        // if (annotations != null) {
        // for (AnnotationExpr a : annotations) {
        // a.accept(this, arg);
        // printer.printLn();
        // }
        // }
        printModifiers(n.getModifiers());

        n.getType().accept(this, arg);
        printer.print(" ");
        printer.print(n.getName());
        currentAnnotations = null;
        printer.print("()");
        if (n.getDefaultValue() != null) {
            printer.print(" default ");
            n.getDefaultValue().accept(this, arg);
        }
        printer.print(";");
    }

    public void visit(MarkerAnnotationExpr n, Object arg) {
        printer.print("@");
        n.getName().accept(this, arg);
    }

    public void visit(SingleMemberAnnotationExpr n, Object arg) {
        printer.print("@");
        n.getName().accept(this, arg);
        printer.print("(");
        n.getMemberValue().accept(this, arg);
        printer.print(")");
    }

    public void visit(NormalAnnotationExpr n, Object arg) {
        printer.print("@");
        n.getName().accept(this, arg);
        printer.print("(");
        if (n.getPairs() != null) {
            for (Iterator<MemberValuePair> i = n.getPairs().iterator(); i.hasNext();) {
                MemberValuePair m = i.next();
                m.accept(this, arg);
                if (i.hasNext()) {
                    printer.print(", ");
                }
            }
        }
        printer.print(")");
    }

    public void visit(MemberValuePair n, Object arg) {
        printer.print(n.getName());
        printer.print(" = ");
        n.getValue().accept(this, arg);
    }

    public void visit(LineComment n, Object arg) {
        printer.print("//");
        printer.printLn(n.getContent());
    }

    public void visit(BlockComment n, Object arg) {
        printer.print("/*");
        printer.print(n.getContent());
        printer.printLn("*/");
    }

}