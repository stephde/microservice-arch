package com.modelgenerator;

import de.mdelab.comparch.Architecture;
import de.mdelab.comparch.ComparchPackage;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

@Service
@Slf4j
public class CompArchLoader {


    public Architecture loadModel() throws RuntimeException {
        ComparchPackage.eINSTANCE.eClass();
        String modelURI = "./model/mRUBiS.comparch";
        Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
        Map<String, Object> m = reg.getExtensionToFactoryMap();
        m.put("comparch", new XMIResourceFactoryImpl());
        ResourceSet resourceSet = new ResourceSetImpl();
        Resource resource = resourceSet.getResource(URI.createURI(modelURI), true);
        Architecture architecture = (Architecture) resource.getContents().get(0);

        log.info("Loaded model from: {}", modelURI);
        this.logArchitecture(architecture);

        return architecture;
    }

    public void saveModel(Architecture model) {
        String modelURI = "./model/generatedModel.comparch";
        log.info("Saving model to {}", modelURI);

        ResourceSet rs = new ResourceSetImpl();
        URI uri = URI.createFileURI(modelURI);
        Resource snapshot = rs.createResource(uri);
        snapshot.getContents().add(EcoreUtil.copy(model));
        try {
            snapshot.save(Collections.EMPTY_MAP);
        } catch (IOException e) {
            log.warn("Cannot save model to file {}", model);
            e.printStackTrace();
        }
    }

    private void logArchitecture(Architecture architecture) {
        System.out.println(architecture.toString());
        architecture.getComponentTypes().forEach(c -> {
            System.out.println(c.getName());
            System.out.println(c.getInstances().toString());
            System.out.println(c.getParameterTypes().toString());
        });
    }
}
