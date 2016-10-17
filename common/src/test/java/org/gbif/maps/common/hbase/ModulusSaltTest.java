package org.gbif.maps.common.hbase;

import org.junit.Test;

import static org.junit.Assert.*;

public class ModulusSaltTest {

  /**
   * Perhaps rather pointless tests, but they show correct behaviour at least.
   */
  @Test
  public void testSaltToString() {
    ModulusSalt salt = new ModulusSalt(8);
    String s = "bingo";
    int mod = s.hashCode() % 8;
    assertEquals(mod + ":" + s, salt.saltToString(s));

    salt = new ModulusSalt(1000); // 0->999
    mod = s.hashCode() % 1000;
    assertEquals(mod + ":" + s, salt.saltToString(s));

    salt = new ModulusSalt(28); // 0->998
    mod = s.hashCode() % 28;
    assertEquals("03:" + s, salt.saltToString(s));

  }

  @Test
  public void testPadding() {
    assertEquals("001", ModulusSalt.leftPadZeros(1, 3)) ;
  }


  @Test
  public void testDigitCount() {
    assertEquals(1,ModulusSalt.digitCount(1));
    assertEquals(1,ModulusSalt.digitCount(9));
    assertEquals(2,ModulusSalt.digitCount(10));
    assertEquals(2,ModulusSalt.digitCount(19));
    assertEquals(3,ModulusSalt.digitCount(100));
    assertEquals(5,ModulusSalt.digitCount(12345));
  }

}
