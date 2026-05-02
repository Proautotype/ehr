package com.custard.ehr;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;
import org.springframework.modulith.docs.Documenter.CanvasOptions;
import org.springframework.modulith.docs.Documenter.DiagramOptions;

@SpringBootTest
class EhrApplicationTests {
	ApplicationModules modules = ApplicationModules.of(EhrApplication.class);

	@Test
	void verifyModularity() {
		// --> Module model
		System.out.println(modules);
		// --> Trigger verification
		modules.verify();
	}
	@Test
	void testmodule(){
		var canvasOptions = CanvasOptions.defaults();

		var docOptions = DiagramOptions.defaults()
				.withStyle(DiagramOptions.DiagramStyle.UML);

		new Documenter(modules) //
				.writeDocumentation(docOptions, canvasOptions);
	}

}
