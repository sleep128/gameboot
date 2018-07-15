package com.gp.common.plugin;

import org.apache.velocity.app.VelocityEngine;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class GengratorBoCodeNew {

    public static void main(String[] args) throws Exception {

        boolean overwrited = false;

        String basePath = "/Users/zhangyang/DEV/gitRepo/gameboot";

        String boPath = "src/main/java/com/gp/bo";

        String modelPath = "src/main/java/com/gp/dao/model";

        String boImplPath = "src/main/java/com/gp/bo/impl";

        File file = new File(basePath, modelPath);

        String[] fileNames = file.list(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                return !"Page.java".equals(name) && !name.endsWith("Example.java") && !"BaseModel.java".equals(name);
            }
        });

        Properties properties = new Properties();
        properties.setProperty("resource.loader", "class");
        properties.setProperty("class.resource.loader.class",
                               "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        VelocityEngine velocityEngine = new VelocityEngine(properties);
        velocityEngine.init();
        File servicePath = new File(basePath, boPath);
        for (String fileName : fileNames) {
            fileName = fileName.substring(0, fileName.lastIndexOf("."));
            File serviceFile = new File(servicePath, fileName + "Bo.java");
            if (!overwrited && serviceFile.exists()) {
                continue;
            }
            System.out.println("gengrator " + serviceFile.getName());
            BufferedWriter writer = new BufferedWriter(new FileWriter(serviceFile));

            VelocityEngineUtils.mergeTemplate(velocityEngine, "bo-templateNew.vm", "UTF-8",
                                              Collections.singletonMap("name", (Object) fileName), writer);
            writer.flush();
            writer.close();
        }

        File serviceImplPath = new File(basePath, boImplPath);
        for (String fileName : fileNames) {
            fileName = fileName.substring(0, fileName.lastIndexOf("."));
            File implFile = new File(serviceImplPath, fileName + "BoImpl.java");
            if (!overwrited && implFile.exists()) {
                continue;
            }
            System.out.println("gengrator " + implFile.getName());
            BufferedWriter writer = new BufferedWriter(new FileWriter(implFile));
            Map<String, Object> context = new HashMap<String, Object>();
            context.put("name", fileName);
            context.put("caseName", String.valueOf(fileName.charAt(2)).toLowerCase() + fileName.substring(3));
            VelocityEngineUtils.mergeTemplate(velocityEngine, "boimpl-templateNew.vm", "UTF-8", context, writer);
            writer.flush();
            writer.close();
        }
        System.out.println("done gengrator");
    }

}
