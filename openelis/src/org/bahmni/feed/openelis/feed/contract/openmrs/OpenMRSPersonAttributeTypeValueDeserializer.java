package org.bahmni.feed.openelis.feed.contract.openmrs;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.ObjectCodec;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.node.TextNode;

import java.io.IOException;

/*
* The contents of this file are subject to the Mozilla Public License
* Version 1.1 (the "License"); you may not use this file except in
* compliance with the License. You may obtain a copy of the License at
* http://www.mozilla.org/MPL/
*
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations under
* the License.
*
* The Original Code is OpenELIS code.
*
* Copyright (C) The Minnesota Department of Health.  All Rights Reserved.
*/
public class OpenMRSPersonAttributeTypeValueDeserializer extends JsonDeserializer<OpenMRSPersonAttributeTypeValue> {

    @Override
    public OpenMRSPersonAttributeTypeValue deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {

        ObjectCodec oc = jsonParser.getCodec();
        JsonNode node = oc.readTree(jsonParser);

        JsonNode display = node.get("display");

        if (display == null){
            return createResult(node);
        }

        return createResult(display);
    }

    private OpenMRSPersonAttributeTypeValue createResult(JsonNode node) {
        OpenMRSPersonAttributeTypeValue openMRSPersonAttributeTypeValue = new OpenMRSPersonAttributeTypeValue();
        openMRSPersonAttributeTypeValue.setDisplay(node.asText());
        return  openMRSPersonAttributeTypeValue;
    }
}
