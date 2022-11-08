/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package esa.mo.mal.impl.broker;

import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author mansuruddin.khan
 */
public class BrokerMatcherTest {
    
    public BrokerMatcherTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of domainMatchesWildcardDomain method, of class BrokerMatcher.
     */
    @Test
    public void testDomainMatchesWildcardDomain_1_1() {
        System.out.println("domainMatchesWildcardDomain: both domains are null");
        IdentifierList consumerDomain = null;
        IdentifierList providerDomain = null;
        boolean expResult = true;
        boolean result = BrokerMatcher.domainMatchesWildcardDomain(consumerDomain, providerDomain);
        assertEquals(expResult, result);
    }
    @Test
    public void testDomainMatchesWildcardDomain_1_2() {
        System.out.println("domainMatchesWildcardDomain: only consumerDomain is null");
        IdentifierList consumerDomain = null;
        IdentifierList providerDomain =  new IdentifierList();
        providerDomain.add(new Identifier("spacecraftA"));
        boolean expResult = false;
        boolean result = BrokerMatcher.domainMatchesWildcardDomain(consumerDomain, providerDomain);
        assertEquals(expResult, result);
    }    
    @Test
    public void testDomainMatchesWildcardDomain_1_3() {
        System.out.println("domainMatchesWildcardDomain: only consumerDomain is null");
        IdentifierList consumerDomain = new IdentifierList();
        IdentifierList providerDomain =  null;
        consumerDomain.add(new Identifier("spacecraftA"));
        boolean expResult = false;
        boolean result = BrokerMatcher.domainMatchesWildcardDomain(consumerDomain, providerDomain);
        assertEquals(expResult, result);
    }      
    @Test
    public void testDomainMatchesWildcardDomain_2_1() {
        System.out.println("domainMatchesWildcardDomain: *.payload.cameraA.* and spacecraftA.payload.cameraA.tempB matching");
        IdentifierList consumerDomain = new IdentifierList();        
        consumerDomain.add(new Identifier("*"));
        consumerDomain.add(new Identifier("payload"));
        consumerDomain.add(new Identifier("cameraA"));
        consumerDomain.add(new Identifier("*"));
        
        IdentifierList providerDomain =  new IdentifierList();
        providerDomain.add(new Identifier("spacecraftA"));
        providerDomain.add(new Identifier("payload"));
        providerDomain.add(new Identifier("cameraA"));
        providerDomain.add(new Identifier("tempB"));
        
        boolean expResult = true;
        boolean result = BrokerMatcher.domainMatchesWildcardDomain(consumerDomain, providerDomain);
        assertEquals(expResult, result);
    }     
    @Test
    public void testDomainMatchesWildcardDomain_2_2() {
        System.out.println("domainMatchesWildcardDomain: *.payload.cameraA.* and spacecraftB.payload.cameraA.tempB matching");
        IdentifierList consumerDomain = new IdentifierList();        
        consumerDomain.add(new Identifier("*"));
        consumerDomain.add(new Identifier("payload"));
        consumerDomain.add(new Identifier("cameraA"));
        consumerDomain.add(new Identifier("*"));
        
        IdentifierList providerDomain =  new IdentifierList();
        providerDomain.add(new Identifier("spacecraftB"));
        providerDomain.add(new Identifier("payload"));
        providerDomain.add(new Identifier("cameraA"));
        providerDomain.add(new Identifier("tempB"));
        
        boolean expResult = true;
        boolean result = BrokerMatcher.domainMatchesWildcardDomain(consumerDomain, providerDomain);
        assertEquals(expResult, result);
    }
    @Test
    public void testDomainMatchesWildcardDomain_2_3() {
        System.out.println("domainMatchesWildcardDomain: *.payload.cameraA.* and spacecraftB.payload.cameraB.tempB matching");
        IdentifierList consumerDomain = new IdentifierList();        
        consumerDomain.add(new Identifier("*"));
        consumerDomain.add(new Identifier("payload"));
        consumerDomain.add(new Identifier("cameraA"));
        consumerDomain.add(new Identifier("*"));
        
        IdentifierList providerDomain =  new IdentifierList();
        providerDomain.add(new Identifier("spacecraftB"));
        providerDomain.add(new Identifier("payload"));
        providerDomain.add(new Identifier("cameraB"));
        providerDomain.add(new Identifier("tempB"));
        
        boolean expResult = false;
        boolean result = BrokerMatcher.domainMatchesWildcardDomain(consumerDomain, providerDomain);
        assertEquals(expResult, result);
    }    
    @Test
    public void testDomainMatchesWildcardDomai_2_4() {
        System.out.println("domainMatchesWildcardDomain: *.payload.cameraA.* and payload.cameraA matching");
        IdentifierList consumerDomain = new IdentifierList();        
        consumerDomain.add(new Identifier("*"));
        consumerDomain.add(new Identifier("payload"));
        consumerDomain.add(new Identifier("cameraA"));
        consumerDomain.add(new Identifier("*"));
        
        IdentifierList providerDomain =  new IdentifierList();
        providerDomain.add(new Identifier("payload"));
        providerDomain.add(new Identifier("cameraA"));
        
        boolean expResult = true;
        boolean result = BrokerMatcher.domainMatchesWildcardDomain(consumerDomain, providerDomain);
        assertEquals(expResult, result);
    }    
    @Test
    public void testDomainMatchesWildcardDomain_2_5() {
        System.out.println("domainMatchesWildcardDomain: *.payload.cameraA.* and spacecraftB.payload.cameraB.tempB matching");
        IdentifierList consumerDomain = new IdentifierList();        
        consumerDomain.add(new Identifier("*"));
        consumerDomain.add(new Identifier("payload"));
        consumerDomain.add(new Identifier("cameraA"));
        consumerDomain.add(new Identifier("*"));
        
        IdentifierList providerDomain =  new IdentifierList();
        providerDomain.add(new Identifier("payload"));
        providerDomain.add(new Identifier("cameraB"));
        
        boolean expResult = false;
        boolean result = BrokerMatcher.domainMatchesWildcardDomain(consumerDomain, providerDomain);
        assertEquals(expResult, result);
    }     
    @Test
    public void testDomainMatchesWildcardDomain_3_1() {
        System.out.println("domainMatchesWildcardDomain: spacecraftA and spacecraftA matching");
        IdentifierList consumerDomain = new IdentifierList();        
        consumerDomain.add(new Identifier("spacecraftA"));
        
        IdentifierList providerDomain =  new IdentifierList();
        providerDomain.add(new Identifier("spacecraftA"));
        
        boolean expResult = true;
        boolean result = BrokerMatcher.domainMatchesWildcardDomain(consumerDomain, providerDomain);
        assertEquals(expResult, result);
    }    
    @Test
    public void testDomainMatchesWildcardDomain_3_2() {
        System.out.println("domainMatchesWildcardDomain: spacecraftA and spacecraftB matching");
        IdentifierList consumerDomain = new IdentifierList();        
        consumerDomain.add(new Identifier("spacecraftA"));
        
        IdentifierList providerDomain =  new IdentifierList();
        providerDomain.add(new Identifier("spacecraftB"));
        
        boolean expResult = false;
        boolean result = BrokerMatcher.domainMatchesWildcardDomain(consumerDomain, providerDomain);
        assertEquals(expResult, result);
    }      
    @Test
    public void testDomainMatchesWildcardDomain_3_3() {
        System.out.println("domainMatchesWildcardDomain: spacecraftA.aocs and spacecraftA.aocs matching");
        IdentifierList consumerDomain = new IdentifierList();        
        consumerDomain.add(new Identifier("spacecraftA"));
        consumerDomain.add(new Identifier("aocs"));
        
        IdentifierList providerDomain =  new IdentifierList();
        providerDomain.add(new Identifier("spacecraftA"));
        providerDomain.add(new Identifier("aocs"));
        
        boolean expResult = true;
        boolean result = BrokerMatcher.domainMatchesWildcardDomain(consumerDomain, providerDomain);
        assertEquals(expResult, result);
    }   
    @Test
    public void testDomainMatchesWildcardDomain_4_1() {
        System.out.println("domainMatchesWildcardDomain: spacecraftA.payload.* and spacecraftA.payload.cameraA.tempB matching");
        IdentifierList consumerDomain = new IdentifierList();        
        consumerDomain.add(new Identifier("spacecraftA"));
        consumerDomain.add(new Identifier("payload"));
        consumerDomain.add(new Identifier("*"));
        
        IdentifierList providerDomain =  new IdentifierList();
        providerDomain.add(new Identifier("spacecraftA"));
        providerDomain.add(new Identifier("payload"));
        providerDomain.add(new Identifier("cameraA"));
        providerDomain.add(new Identifier("tempB"));
        
        boolean expResult = true;
        boolean result = BrokerMatcher.domainMatchesWildcardDomain(consumerDomain, providerDomain);
        assertEquals(expResult, result);
    }  
    @Test
    public void testDomainMatchesWildcardDomain_4_2() {
        System.out.println("domainMatchesWildcardDomain: spacecraftA.* and spacecraftA matching");
        IdentifierList consumerDomain = new IdentifierList();        
        consumerDomain.add(new Identifier("spacecraftA"));
        consumerDomain.add(new Identifier("*"));
        
        IdentifierList providerDomain =  new IdentifierList();
        providerDomain.add(new Identifier("spacecraftA"));
        
        boolean expResult = true;
        boolean result = BrokerMatcher.domainMatchesWildcardDomain(consumerDomain, providerDomain);
        assertEquals(expResult, result);
    }    
    @Test
    public void testDomainMatchesWildcardDomain_4_3() {
        System.out.println("domainMatchesWildcardDomain: spacecraftA.* and spacecraftA.aocs matching");
        IdentifierList consumerDomain = new IdentifierList();        
        consumerDomain.add(new Identifier("spacecraftA"));
        consumerDomain.add(new Identifier("*"));
        
        IdentifierList providerDomain =  new IdentifierList();
        providerDomain.add(new Identifier("spacecraftA"));
        providerDomain.add(new Identifier("aocs"));
        
        boolean expResult = true;
        boolean result = BrokerMatcher.domainMatchesWildcardDomain(consumerDomain, providerDomain);
        assertEquals(expResult, result);
    }    
    @Test
    public void testDomainMatchesWildcardDomain_4_4() {
        System.out.println("domainMatchesWildcardDomain: spacecraftA.* and spacecraftA.aocs.thrustA matching");
        IdentifierList consumerDomain = new IdentifierList();        
        consumerDomain.add(new Identifier("spacecraftA"));
        consumerDomain.add(new Identifier("*"));
        
        IdentifierList providerDomain =  new IdentifierList();
        providerDomain.add(new Identifier("spacecraftA"));
        providerDomain.add(new Identifier("aocs"));
        providerDomain.add(new Identifier("thrustA"));
        
        boolean expResult = true;
        boolean result = BrokerMatcher.domainMatchesWildcardDomain(consumerDomain, providerDomain);
        assertEquals(expResult, result);
    }        
    @Test
    public void testDomainMatchesWildcardDomain_4_5() {
        System.out.println("domainMatchesWildcardDomain: spacecraftA.* and spacecraftA.payload matching");
        IdentifierList consumerDomain = new IdentifierList();        
        consumerDomain.add(new Identifier("spacecraftA"));
        consumerDomain.add(new Identifier("*"));
        
        IdentifierList providerDomain =  new IdentifierList();
        providerDomain.add(new Identifier("spacecraftA"));
        providerDomain.add(new Identifier("payload"));
        
        boolean expResult = true;
        boolean result = BrokerMatcher.domainMatchesWildcardDomain(consumerDomain, providerDomain);
        assertEquals(expResult, result);
    }    
    @Test
    public void testDomainMatchesWildcardDomain_4_6() {
        System.out.println("domainMatchesWildcardDomain: spacecraftA.* and spacecraftA.payload.cameraA.tempB matching");
        IdentifierList consumerDomain = new IdentifierList();        
        consumerDomain.add(new Identifier("spacecraftA"));
        consumerDomain.add(new Identifier("*"));
        
        IdentifierList providerDomain =  new IdentifierList();
        providerDomain.add(new Identifier("spacecraftA"));
        providerDomain.add(new Identifier("payload"));
        providerDomain.add(new Identifier("cameraA"));
        providerDomain.add(new Identifier("tempB"));
        
        boolean expResult = true;
        boolean result = BrokerMatcher.domainMatchesWildcardDomain(consumerDomain, providerDomain);
        assertEquals(expResult, result);
    }     
    @Test
    public void testDomainMatchesWildcardDomain_5_1() {
        System.out.println("domainMatchesWildcardDomain: *.tempB and cameraA.tempB matching");
        IdentifierList consumerDomain = new IdentifierList();        
        consumerDomain.add(new Identifier("*"));
        consumerDomain.add(new Identifier("tempB"));
        
        IdentifierList providerDomain =  new IdentifierList();
        providerDomain.add(new Identifier("cameraA"));
        providerDomain.add(new Identifier("tempB"));
        
        boolean expResult = true;
        boolean result = BrokerMatcher.domainMatchesWildcardDomain(consumerDomain, providerDomain);
        assertEquals(expResult, result);
    }
    @Test
    public void testDomainMatchesWildcardDomain_5_2() {
        System.out.println("domainMatchesWildcardDomain: *.tempB and payload.cameraA.tempB matching");
        IdentifierList consumerDomain = new IdentifierList();        
        consumerDomain.add(new Identifier("*"));
        consumerDomain.add(new Identifier("tempB"));
        
        IdentifierList providerDomain =  new IdentifierList();
        providerDomain.add(new Identifier("payload"));
        providerDomain.add(new Identifier("cameraA"));
        providerDomain.add(new Identifier("tempB"));
        
        boolean expResult = true;
        boolean result = BrokerMatcher.domainMatchesWildcardDomain(consumerDomain, providerDomain);
        assertEquals(expResult, result);
    }  
    @Test
    public void testDomainMatchesWildcardDomain_5_3() {
        System.out.println("domainMatchesWildcardDomain: *.tempA and payload.cameraA.tempB matching");
        IdentifierList consumerDomain = new IdentifierList();        
        consumerDomain.add(new Identifier("*"));
        consumerDomain.add(new Identifier("tempA"));
        
        IdentifierList providerDomain =  new IdentifierList();
        providerDomain.add(new Identifier("payload"));
        providerDomain.add(new Identifier("cameraA"));
        providerDomain.add(new Identifier("tempB"));
        
        boolean expResult = false;
        boolean result = BrokerMatcher.domainMatchesWildcardDomain(consumerDomain, providerDomain);
        assertEquals(expResult, result);
    }  
    @Test
    public void testDomainMatchesWildcardDomain_5_4() {
        System.out.println("domainMatchesWildcardDomain: *.tempB and tempB matching");
        IdentifierList consumerDomain = new IdentifierList();        
        consumerDomain.add(new Identifier("*"));
        consumerDomain.add(new Identifier("tempB"));
        
        IdentifierList providerDomain =  new IdentifierList();
        providerDomain.add(new Identifier("tempB"));
        
        boolean expResult = true;
        boolean result = BrokerMatcher.domainMatchesWildcardDomain(consumerDomain, providerDomain);
        assertEquals(expResult, result);
    }   
    @Test
    public void testDomainMatchesWildcardDomain_5_5() {
        System.out.println("domainMatchesWildcardDomain: *.tempB and tempA matching");
        IdentifierList consumerDomain = new IdentifierList();        
        consumerDomain.add(new Identifier("*"));
        consumerDomain.add(new Identifier("tempB"));
        
        IdentifierList providerDomain =  new IdentifierList();
        providerDomain.add(new Identifier("tempA"));
        
        boolean expResult = false;
        boolean result = BrokerMatcher.domainMatchesWildcardDomain(consumerDomain, providerDomain);
        assertEquals(expResult, result);
    }       
}