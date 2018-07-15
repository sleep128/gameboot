package com.gp.common.plugin;

import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.*;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.*;
import org.mybatis.generator.codegen.XmlConstants;
import org.mybatis.generator.config.PropertyRegistry;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.util.*;

/**
 * 数据库插件
 * 
 * @author system
 */
public class MysqlPaginationPluginNew extends PluginAdapter {

    private static String FULLY_QUALIFIED_PAGE = "com.gp.common.util.Page";

    private static String XMLFILE_POSTFIX = "Ext";

    private static String JAVAFILE_POTFIX = "Ext";





    private static String ANNOTATION_RESOURCE = "org.apache.ibatis.annotations.Mapper";

    private static String ANNOTATION_PARAM = "org.apache.ibatis.annotations.Param";

    private static Map<String, List<Element>> INSERT_MAP = new HashMap<>();

    @Override
    public boolean modelExampleClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        // 增加翻页
        addPage(topLevelClass, introspectedTable, "page");
        addCredCriteriaIsValidMethod(topLevelClass, introspectedTable);
        return super.modelExampleClassGenerated(topLevelClass, introspectedTable);
    }

    /**
     * 在XXExample对象里添加Page对象属性
     * 
     * @param topLevelClass
     * @param introspectedTable
     * @param name
     */
    private void addPage(TopLevelClass topLevelClass, IntrospectedTable introspectedTable, String name) {
        topLevelClass.addImportedType(new FullyQualifiedJavaType(FULLY_QUALIFIED_PAGE));
        CommentGenerator commentGenerator = context.getCommentGenerator();
        Field field = new Field();
        field.setVisibility(JavaVisibility.PROTECTED);
        field.setType(new FullyQualifiedJavaType(FULLY_QUALIFIED_PAGE));
        field.setName(name);
        commentGenerator.addFieldComment(field, introspectedTable);
        topLevelClass.addField(field);
        char c = name.charAt(0);
        String camel = Character.toUpperCase(c) + name.substring(1);
        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setName("set" + camel);
        method.addParameter(new Parameter(new FullyQualifiedJavaType(FULLY_QUALIFIED_PAGE), name));
        method.addBodyLine("this." + name + " = " + name + ";");
        commentGenerator.addGeneralMethodComment(method, introspectedTable);
        topLevelClass.addMethod(method);
        method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(new FullyQualifiedJavaType(FULLY_QUALIFIED_PAGE));
        method.setName("get" + camel);
        method.addBodyLine("return " + name + ";");
        commentGenerator.addGeneralMethodComment(method, introspectedTable);
        topLevelClass.addMethod(method);
    }

    private void addCredCriteriaIsValidMethod(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        CommentGenerator commentGenerator = context.getCommentGenerator();
        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(FullyQualifiedJavaType.getBooleanPrimitiveInstance());
        method.setName("isValid");
        List<String> bodyLines = new ArrayList<>();
        bodyLines.add("if (oredCriteria.isEmpty()) {");
        bodyLines.add("return false;");
        bodyLines.add("}");
        bodyLines.add("for (Criteria criteria : oredCriteria) {");
        bodyLines.add("if (criteria.isValid()) {");
        bodyLines.add("return true;");
        bodyLines.add("}");
        bodyLines.add("}");
        bodyLines.add("return false;");
        method.addBodyLines(bodyLines);
        commentGenerator.addGeneralMethodComment(method, introspectedTable);
        topLevelClass.addMethod(method);
    }

    // 添删改Document的sql语句及属性
    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {

        XmlElement parentElement = document.getRootElement();

        updateDocumentNameSpace(introspectedTable, parentElement);

        removeDocumentUpdateByPrimaryKeySql(parentElement);

        generateInsertSql(parentElement, introspectedTable);

        generateMysqlPageSql(parentElement, introspectedTable);

        return super.sqlMapDocumentGenerated(document, introspectedTable);
    }

    private void generateInsertSql(XmlElement parentElement, IntrospectedTable introspectedTable) {
        String tableName = introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime();
        List<Element> elements = INSERT_MAP.get(tableName);
        if (CollectionUtils.isEmpty(elements)) {
            return;
        }
        // mysql分页语句后半部分
        XmlElement paginationSuffixElement = new XmlElement("sql");
        paginationSuffixElement.addAttribute(new Attribute("id", "Insert_Column"));
        for (Element element : elements) {
            paginationSuffixElement.addElement(element);
        }
        parentElement.addElement(paginationSuffixElement);
        INSERT_MAP.remove(tableName);
    }

    private void generateMysqlPageSql(XmlElement parentElement, IntrospectedTable introspectedTable) {
        // mysql分页语句后半部分
        XmlElement paginationSuffixElement = new XmlElement("sql");
        context.getCommentGenerator().addComment(paginationSuffixElement);
        paginationSuffixElement.addAttribute(new Attribute("id", "MysqlPagination"));
        XmlElement pageEnd = new XmlElement("if");
        pageEnd.addAttribute(new Attribute("test", "page != null"));
        pageEnd.addElement(new TextElement("<![CDATA[ limit #{page.begin}, #{page.length} ]]>"));
        paginationSuffixElement.addElement(pageEnd);

        parentElement.addElement(paginationSuffixElement);
    }

    private void removeDocumentUpdateByPrimaryKeySql(XmlElement parentElement) {
        XmlElement updateElement = null;
        for (Element element : parentElement.getElements()) {
            XmlElement xmlElement = (XmlElement)element;
            if ("update".equals(xmlElement.getName())) {
                for (Attribute attribute : xmlElement.getAttributes()) {
                    if ("updateByPrimaryKey".equals(attribute.getValue())) {
                        updateElement = xmlElement;
                        break;
                    }
                }
            }
        }
        parentElement.getElements().remove(updateElement);
    }

    private void updateDocumentNameSpace(IntrospectedTable introspectedTable, XmlElement parentElement) {
        Attribute namespaceAttribute = null;
        for (Attribute attribute : parentElement.getAttributes()) {
            if ("namespace".equals(attribute.getName())) {
                namespaceAttribute = attribute;
            }
        }
        parentElement.getAttributes().remove(namespaceAttribute);
        parentElement.getAttributes().add(new Attribute("namespace", introspectedTable.getMyBatis3JavaMapperType()
            + JAVAFILE_POTFIX));
    }

    @Override
    public boolean sqlMapSelectByPrimaryKeyElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        return super.sqlMapSelectByPrimaryKeyElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean sqlMapUpdateByPrimaryKeySelectiveElementGenerated(XmlElement element,
        IntrospectedTable introspectedTable) {
        List<Element> elements = element.getElements();
        XmlElement setItem = null;
        for (Element e : elements) {
            if (e instanceof XmlElement) {
                setItem = (XmlElement)e;
                Iterator<Element> it = setItem.getElements().iterator();
                while (it.hasNext()) {
                    XmlElement xmlElement = (XmlElement)it.next();
                    for (Attribute att : xmlElement.getAttributes()) {
                        String value = att.getValue();
//                        if ("creator != null".equals(value) || "isDeleted != null".equals(value)
//                            || "creator != null".equals(value) || "gmtCreate != null".equals(value)) {
//                            it.remove();
//                           }
                        }
                    }
                }
            }
        return super.sqlMapUpdateByPrimaryKeySelectiveElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean sqlMapUpdateByPrimaryKeyWithoutBLOBsElementGenerated(XmlElement element,
        IntrospectedTable introspectedTable) {
        return super.sqlMapUpdateByPrimaryKeyWithoutBLOBsElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean sqlMapDeleteByPrimaryKeyElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        element.setName("delete");
        replaceParameterType(element, introspectedTable);
        element.getElements().remove(element.getElements().size() - 1);
        element.getElements().remove(element.getElements().size() - 1);
        String tableName = introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime();
        element.getElements().add(new TextElement("delete from " + tableName
            + " where id = #{id,jdbcType=INTEGER}"));
        return super.sqlMapDeleteByPrimaryKeyElementGenerated(element, introspectedTable);
    }

    private void replaceParameterType(XmlElement element, IntrospectedTable introspectedTable) {
        int replaceInd = -1;
        for (int i = 0; i < element.getAttributes().size(); i++) {
            Attribute attr = element.getAttributes().get(i);
            if ("parameterType".equals(attr.getName())) {
                replaceInd = i;
                break;
            }
        }
        if (replaceInd >= 0) {
            element.getAttributes().remove(replaceInd);
            element.getAttributes().add(replaceInd,
                new Attribute("parameterType", introspectedTable.getBaseRecordType()));
        }
    }

    private void replaceParameterType(XmlElement element, String name, String value) {
        int replaceInd = -1;
        for (int i = 0; i < element.getAttributes().size(); i++) {
            Attribute attr = element.getAttributes().get(i);
            if (attr.getName().equals(name)) {
                replaceInd = i;
                break;
            }
        }
        if (replaceInd >= 0) {
            element.getAttributes().remove(replaceInd);
            if (StringUtils.isNotBlank(value)) {
                element.getAttributes().add(replaceInd, new Attribute(name, value));
            }
        }
    }

    // insert
    @Override
    public boolean sqlMapInsertSelectiveElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        INSERT_MAP.put(introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime(),
            new ArrayList<>(element.getElements()));
        element.getElements().clear();
        context.getCommentGenerator().addComment(element);
        XmlElement insertElement = new XmlElement("include");
        insertElement.addAttribute(new Attribute("refid", "Insert_Column"));
        element.getElements().add(insertElement);

        return super.sqlMapInsertSelectiveElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean sqlMapInsertElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        replaceParameterType(element, "id", "insertMapSelective");
        replaceParameterType(element, "parameterType", "java.util.Map");
        element.getElements().clear();
        context.getCommentGenerator().addComment(element);
        XmlElement insertElement = new XmlElement("include");
        insertElement.addAttribute(new Attribute("refid", "Insert_Column"));
        element.getElements().add(insertElement);
        return super.sqlMapInsertElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean clientDeleteByPrimaryKeyMethodGenerated(Method method, Interface interfaze,
        IntrospectedTable introspectedTable) {
        Parameter parameter = new Parameter(new FullyQualifiedJavaType(introspectedTable.getBaseRecordType()),
            "record");
        method.getParameters().clear();
        method.addParameter(parameter);
        return super.clientDeleteByPrimaryKeyMethodGenerated(method, interfaze, introspectedTable);
    }

    @Override
    public boolean clientInsertMethodGenerated(Method method, Interface interfaze,
        IntrospectedTable introspectedTable) {
        FullyQualifiedJavaType annotation = new FullyQualifiedJavaType("java.util.Map");
        interfaze.addImportedType(annotation);
        method.setName("insertMapSelective");
        Parameter parameter = new Parameter(new FullyQualifiedJavaType("Map<String, Object>"), "record");
        method.getParameters().clear();
        method.addParameter(parameter);
        return super.clientInsertMethodGenerated(method, interfaze, introspectedTable);
    }

    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass,
        IntrospectedTable introspectedTable) {
        List<Method> methods = interfaze.getMethods();
        Iterator<Method> it = methods.iterator();
        while (it.hasNext()) {
            Method method = it.next();
            if ("insert".equals(method.getName()) || "updateByPrimaryKey".equals(method.getName())) {
                it.remove();
            }
        }

        return super.clientGenerated(interfaze, topLevelClass, introspectedTable);
    }

    // selectByExample
    @Override
    public boolean sqlMapSelectByExampleWithoutBLOBsElementGenerated(XmlElement element,
        IntrospectedTable introspectedTable) {

        addWhere(element);
        // $NON-NLS-1$
        XmlElement isNotNullElement = new XmlElement("include");
        isNotNullElement.addAttribute(new Attribute("refid", "MysqlPagination"));
        element.getElements().add(isNotNullElement);

        return super.sqlMapSelectByExampleWithoutBLOBsElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean sqlMapCountByExampleElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {

        addWhere(element);
        return super.sqlMapCountByExampleElementGenerated(element, introspectedTable);
    }

    private void addWhere(XmlElement element) {
        ListIterator<Element> it = element.getElements().listIterator();
        while (it.hasNext()) {
            Element em = it.next();
            if (em instanceof XmlElement) {
                XmlElement e = (XmlElement)em;
                if (e.getAttributes().get(0).getValue().equals("_parameter != null")) {
                    it.previous();
                    it.add(new TextElement(" where 1 = 1 "));
                    return;
                }
            } else if (em instanceof TextElement) {
                TextElement e = (TextElement)em;
                if (e.getContent().contains("'false' as QUERYID")) {
                    it.remove();
                }
            }
        }
    }

    // 生成XXExt.xml
    @Override
    public List<GeneratedXmlFile> contextGenerateAdditionalXmlFiles(IntrospectedTable introspectedTable) {

        String[] splitFile = introspectedTable.getMyBatis3XmlMapperFileName().split("\\.");
        String fileNameExt = null;
        if (splitFile[0] != null) {
            fileNameExt = splitFile[0] + XMLFILE_POSTFIX + ".xml";
        }

        if (isExistExtFile(context.getSqlMapGeneratorConfiguration().getTargetProject(),
            introspectedTable.getMyBatis3XmlMapperPackage(), fileNameExt)) {
            return super.contextGenerateAdditionalXmlFiles(introspectedTable);
        }

        Document document = new Document(XmlConstants.MYBATIS3_MAPPER_PUBLIC_ID,
            XmlConstants.MYBATIS3_MAPPER_SYSTEM_ID);

        XmlElement root = new XmlElement("mapper");
        document.setRootElement(root);
        String namespace = introspectedTable.getMyBatis3SqlMapNamespace() + XMLFILE_POSTFIX;
        root.addAttribute(new Attribute("namespace", namespace));

        GeneratedXmlFile gxf = new GeneratedXmlFile(document, fileNameExt,
            introspectedTable.getMyBatis3XmlMapperPackage(),
            context.getSqlMapGeneratorConfiguration().getTargetProject(), false,
            context.getXmlFormatter());

        List<GeneratedXmlFile> answer = new ArrayList<GeneratedXmlFile>(1);
        answer.add(gxf);

        return answer;
    }

    // 生成XXExt.java
    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {

        FullyQualifiedJavaType type = new FullyQualifiedJavaType(introspectedTable.getMyBatis3JavaMapperType()
            + JAVAFILE_POTFIX);
        Interface interfaze = new Interface(type);
        interfaze.setVisibility(JavaVisibility.PUBLIC);
        context.getCommentGenerator().addJavaFileComment(interfaze);

        FullyQualifiedJavaType baseInterfaze =
            new FullyQualifiedJavaType(introspectedTable.getMyBatis3JavaMapperType());
        interfaze.addSuperInterface(baseInterfaze);

        FullyQualifiedJavaType annotation = new FullyQualifiedJavaType(ANNOTATION_RESOURCE);
        interfaze.addAnnotation("@Mapper");
        interfaze.addImportedType(annotation);

        CompilationUnit compilationUnits = interfaze;
        GeneratedJavaFile generatedJavaFile = new GeneratedJavaFile(compilationUnits,
            context.getJavaClientGeneratorConfiguration().getTargetProject(),
            context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING),
            context.getJavaFormatter());

        if (isExistExtFile(generatedJavaFile.getTargetProject(), generatedJavaFile.getTargetPackage(),
            generatedJavaFile.getFileName())) {
            return super.contextGenerateAdditionalJavaFiles(introspectedTable);
        }
        List<GeneratedJavaFile> generatedJavaFiles = new ArrayList<GeneratedJavaFile>(1);
        generatedJavaFile.getFileName();
        generatedJavaFiles.add(generatedJavaFile);
        return generatedJavaFiles;
    }

    private boolean isExistExtFile(String targetProject, String targetPackage, String fileName) {

        File project = new File(targetProject);
        if (!project.isDirectory()) {
            return true;
        }

        StringBuilder sb = new StringBuilder();
        StringTokenizer st = new StringTokenizer(targetPackage, ".");
        while (st.hasMoreTokens()) {
            sb.append(st.nextToken());
            sb.append(File.separatorChar);
        }

        File directory = new File(project, sb.toString());
        if (!directory.isDirectory()) {
            boolean rc = directory.mkdirs();
            if (!rc) {
                return true;
            }
        }

        File testFile = new File(directory, fileName);
        if (testFile.exists()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean sqlMapExampleWhereClauseElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        Iterator<Element> it = element.getElements().iterator();
        while (it.hasNext()) {
            Element em = (Element)it.next();
            if (em instanceof XmlElement) {
                XmlElement e = (XmlElement)em;
                if ("where".equals(e.getName())) {
                    e.setName("if");
                    e.addAttribute(new Attribute("test", "valid"));
                    // e.addAttribute(new Attribute("test", "oredCriteria.valid"));
                    // XmlElement trimElement = new XmlElement("trim");
                    // trimElement.addAttribute(new Attribute("prefix", "and ("));
                    // trimElement.addAttribute(new Attribute("suffix", ")"));
                    // e.addElement(0, trimElement);
                    // break;
                    XmlElement each = (XmlElement)e.getElements().get(0);
                    if (each.getName().equals("foreach")) {
                        each.addAttribute(new Attribute("open", "and ("));
                        each.addAttribute(new Attribute("close", ")"));
                        break;
                    }
                }
            }

        }
        return true;
    }

    @Override
    public boolean sqlMapDeleteByExampleElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        String tableName = introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime();
            replaceParameterType(element, "id", "deleteById");
            replaceParameterType(element, "parameterType", "java.lang.Long");
            element.getElements().remove(element.getElements().size() - 1);
            element.getElements().remove(element.getElements().size() - 1);
            element.getElements().add(new TextElement("delete 1from " + tableName
                    + " where ID=#{id,jdbcType=BIGINT}"));
            return super.sqlMapDeleteByExampleElementGenerated(element, introspectedTable);

    }

    @Override
    public boolean clientDeleteByExampleMethodGenerated(Method method, Interface interfaze,
        IntrospectedTable introspectedTable) {
        String tableName = introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime();
            method.setName("deleteById");
            Parameter parameter = new Parameter(new FullyQualifiedJavaType("Long"), "id");
            method.getParameters().clear();
            method.addParameter(parameter);
        return true;
    }

    /**
     * This plugin is always valid - no properties are required
     */
    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    public static void main(String[] args) {
        String config = "/Users/zhangyang/DEV/gitRepo/gameboot/src/main/resources/generatorConfigNew.xml";
        String[] arg = {"-configfile", config, "-verbose", "-overwrite"};
        ShellRunner.main(arg);
    }

}
