package org.robolectric.shadows;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.fail;
import static org.robolectric.Shadows.shadowOf;

import android.location.Address;
import android.location.Geocoder;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

/** Unit test for {@link ShadowGeocoder}. */
@RunWith(RobolectricTestRunner.class)
public class ShadowGeocoderTest {

  @Test
  public void isPresentReturnsTrueByDefault() {
    assertThat(Geocoder.isPresent()).isTrue();
  }

  @Test
  public void isPresentReturnsFalseWhenOverridden() {
    ShadowGeocoder.setIsPresent(false);

    assertThat(Geocoder.isPresent()).isFalse();
  }

  @Test
  public void getFromLocationReturnsAnEmptyArrayByDefault() throws IOException {
    Geocoder geocoder = new Geocoder(RuntimeEnvironment.application.getApplicationContext());
    assertThat(geocoder.getFromLocation(90.0,90.0,1)).hasSize(0);
  }

  @Test
  public void getFromLocationReturnsTheOverwrittenListLimitingByMaxResults() throws IOException {
    Geocoder geocoder = new Geocoder(RuntimeEnvironment.application.getApplicationContext());
    ShadowGeocoder shadowGeocoder = shadowOf(geocoder);

    List<Address> list = Arrays.asList(new Address(Locale.getDefault()), new Address(Locale.CANADA));
    shadowGeocoder.setFromLocation(list);

    List<Address> result = geocoder.getFromLocation(90.0, 90.0, 1);
    assertThat(result).hasSize(1);

    result = geocoder.getFromLocation(90.0, 90.0, 2);
    assertThat(result).hasSize(2);

    result = geocoder.getFromLocation(90.0, 90.0, 3);
    assertThat(result).hasSize(2);
  }

  @Test
  public void getFromLocation_throwsExceptionForInvalidLatitude() throws IOException {
    Geocoder geocoder = new Geocoder(RuntimeEnvironment.application.getApplicationContext());

    try {
      geocoder.getFromLocation(91.0, 90.0, 1);
      fail("IllegalArgumentException not thrown");
    } catch (IllegalArgumentException thrown) {
      assertThat(thrown).hasMessageThat().contains(Double.toString(91.0));
    }
  }

  @Test
  public void getFromLocation_throwsExceptionForInvalidLongitude() throws IOException {
    Geocoder geocoder = new Geocoder(RuntimeEnvironment.application.getApplicationContext());

    try {
      geocoder.getFromLocation(15.0, -211.0, 1);
      fail("IllegalArgumentException not thrown");
    } catch (IllegalArgumentException thrown) {
      assertThat(thrown).hasMessageThat().contains(Double.toString(-211.0));
    }
  }

  @Test
  public void resettingShadowRestoresDefaultValueForIsPresent() {
    ShadowGeocoder.setIsPresent(false);
    ShadowGeocoder.reset();
    assertThat(Geocoder.isPresent()).isTrue();
  }
}
