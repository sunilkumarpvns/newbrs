package com.elitecore.coreradius.commons.util;

import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.util.dictionary.DictionaryAttributeTypeConstant;
import org.hamcrest.Matcher;

        import static com.elitecore.coreradius.commons.util.TypeMatchers.*;

        public class RadiusMatchers {

            public static class TypeMatcher {

                public static Matcher<? super IRadiusAttribute> ofType(DictionaryAttributeTypeConstant type, long vendorId) {
                        return new AttributeOfType(type, vendorId);
                    }
    }
}
