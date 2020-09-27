/* Copyright (c) 2011 Danish Maritime Authority.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dk.dma.ais.message.binary;

import dk.dma.ais.binary.BinArray;
import dk.dma.ais.binary.SixbitException;

/**
 * The type Broadcast route information.
 */
public class BroadcastRouteInformation extends RouteInformation {

    /**
     * Instantiates a new Broadcast route information.
     */
    public BroadcastRouteInformation() {
        super(1, 27);
    }

    /**
     * Instantiates a new Broadcast route information.
     *
     * @param binArray the bin array
     * @throws SixbitException the sixbit exception
     */
    public BroadcastRouteInformation(BinArray binArray) throws SixbitException {
        super(1, 27, binArray);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("BroadcastRouteInformation [");
        builder.append(super.toString());
        builder.append("]");
        return builder.toString();
    }

}
