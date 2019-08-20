package com.elitecore.nvsmx.policydesigner.controller.util;

import com.elitecore.commons.tests.PrintMethodRule;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import javax.servlet.http.HttpServletRequest;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Created by aditya on 7/7/17.
 */
@RunWith(HierarchicalContextRunner.class)
public class PkgTypeValidatorCreateMethodTest { //NOSONAR

    @Mock private HttpServletRequest request;


    @Rule public PrintMethodRule printMethodRule = new PrintMethodRule();

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    public class InstantiatePkgTypeValidator {


        @Test
        public void ifRequestIsForDataPkg() {
            when(request.getRequestURI()).thenReturn("policydesigner/pkg/Pkg/create");
            PkgTypeValidator.create(request);

        }

        @Test
        public void ifRequestIsForEmergencyPkg() {
            when(request.getRequestURI()).thenReturn("policydesigner/pkg/EmergencyPkg/create");
            PkgTypeValidator.create(request);
        }

        @Test
        public void ifRequestIsForPromotionalPkg() {
            when(request.getRequestURI()).thenReturn("promotional/policydesigner/pkg/Pkg/PromotionalPkg/create");
            PkgTypeValidator.create(request);
        }


    }

    public class FailedToInstantiatePkgTypeValidator {

        @Rule public ExpectedException exception = ExpectedException.none();


        @Before
        public void setUp() throws Exception {
            exception.expect(NullPointerException.class);
            exception.expectMessage("Acl Module Type can't be null");
        }


        @Test
        public void ifEmptySlashPartitionFound() throws Exception {
            when(request.getRequestURI()).thenReturn("   /   ");
            PkgTypeValidator.create(request);
        }


   }

}