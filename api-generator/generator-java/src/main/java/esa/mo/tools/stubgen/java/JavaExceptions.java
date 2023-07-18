/* ----------------------------------------------------------------------------
 * Copyright (C) 2023      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : CCSDS MO Service Stub Generator
 * ----------------------------------------------------------------------------
 * Licensed under the European Space Agency Public License, Version 2.0
 * You may not use this file except in compliance with the License.
 *
 * Except as expressly set forth in this License, the Software is provided to
 * You on an "as is" basis and without warranties of any kind, including without
 * limitation merchantability, fitness for a particular purpose, absence of
 * defects or errors, accuracy or non-infringement of intellectual property rights.
 * 
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 * ----------------------------------------------------------------------------
 */
package esa.mo.tools.stubgen.java;

import esa.mo.tools.stubgen.ClassWriterProposed;
import esa.mo.tools.stubgen.GeneratorLangs;
import esa.mo.tools.stubgen.specification.CompositeField;
import esa.mo.tools.stubgen.specification.ServiceSummary;
import esa.mo.tools.stubgen.specification.StdStrings;
import esa.mo.tools.stubgen.specification.TypeUtils;
import esa.mo.tools.stubgen.writers.MethodWriter;
import esa.mo.xsd.AreaType;
import esa.mo.xsd.ErrorDefinitionType;
import esa.mo.xsd.ServiceType;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 */
public class JavaExceptions {

    public final static String EXCEPTION = "Exception";
    private final GeneratorLangs generator;

    public JavaExceptions(GeneratorLangs generator) {
        this.generator = generator;
    }

    public void createServiceExceptions(File serviceFolder, AreaType area,
            ServiceType service, ServiceSummary summary) throws IOException {
        generator.getLog().warn("The service Exceptions must be moved to Area "
                + "level! This is just supported for backward compatibility. "
                + "Check the Errors defined in service: " + service.getName());
        generator.getLog().info(" > Creating service Exceptions for service: " + service.getName());

        if (summary.getService().getErrors() != null && summary.getService().getErrors().getError() != null) {
            for (ErrorDefinitionType error : summary.getService().getErrors().getError()) {
                this.generateException(serviceFolder, area, service, error);
            }
        }
    }

    public void createAreaExceptions(File areaFolder, AreaType area) throws IOException {
        generator.getLog().info(" > Creating Area Exceptions for area: " + area.getName());

        if (area.getErrors() != null && area.getErrors().getError() != null) {
            for (ErrorDefinitionType error : area.getErrors().getError()) {
                this.generateException(areaFolder, area, null, error);
            }
        }
    }

    public void generateException(File folder, AreaType area,
            ServiceType service, ErrorDefinitionType error) throws IOException {
        generator.getLog().info(" > Creating Exception: " + error.getName());

        // Needs to be converted to Camel case in the future!
        String inCamelCase = convertToCamelCase(error.getName());
        String className = inCamelCase + EXCEPTION;

        ClassWriterProposed file = generator.createClassFile(folder, className);
        file.addPackageStatement(area, service, null);

        // Appends the class name
        String extendsClass = "org.ccsds.moims.mo.mal.MOErrorException";
        file.addClassOpenStatement(className, true, false, extendsClass,
                null, "The " + className + " exception. " + error.getComment());

        String errorDescription = "\"\"";
        if (error.getExtraInformation() != null) {
            errorDescription = "\"" + error.getExtraInformation().getComment() + "\"";
        }

        // Construct path to Error in the Helper
        String errorNameCaps = error.getName().toUpperCase();
        String errorPath = area.getName() + "Helper." + errorNameCaps + "_ERROR_NUMBER";

        // Constructor without parameters
        MethodWriter method_1 = file.addConstructor(StdStrings.PUBLIC, className,
                null, null, null, "Constructs a new " + className + " exception.", null);
        method_1.addLine("super(" + errorPath + ", " + errorDescription + ")");
        method_1.addMethodCloseStatement();

        // Constructor with a String
        ArrayList<CompositeField> args = new ArrayList<>();
        CompositeField field = generator.createCompositeElementsDetails(file, false, "message",
                TypeUtils.createTypeReference(StdStrings.MAL, null, "String", false),
                false, true, "The message of the exception.");
        args.add(field);

        MethodWriter method_2 = file.addConstructor(StdStrings.PUBLIC, className,
                args, null, null, "Constructs a new " + className + " exception.", null);
        method_2.addLine("super(" + errorPath + ", " + "message)");
        method_2.addMethodCloseStatement();

        file.addClassCloseStatement();
        file.flush();
    }

    public static String convertToCamelCase(String text) {
        // Is it all Upper Case?
        if (text.equals(text.toUpperCase())) {
            StringBuilder all = new StringBuilder();
            // Split by underscore:
            for (String part : text.split("_")) {
                // Convert to Camel case:
                StringBuilder camelCase = new StringBuilder(part.toLowerCase());
                camelCase.setCharAt(0, part.charAt(0));
                all.append(camelCase.toString());
            }

            return all.toString();
        }

        return text;
    }
}
