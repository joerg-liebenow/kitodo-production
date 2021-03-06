/*
 * (c) Kitodo. Key to digital objects e. V. <contact@kitodo.org>
 *
 * This file is part of the Kitodo project.
 *
 * It is licensed under GNU General Public License version 3 or later.
 *
 * For the full copyright and license information, please read the
 * GPL3-License.txt file that was distributed with this source code.
 */

package org.kitodo.dto;

import java.util.List;

public class WorkpieceDTO extends BaseDTO {

    private ProcessDTO process;
    private List<PropertyDTO> properties;

    /**
     * Get list of properties.
     *
     * @return list of properties as PropertyDTO
     */
    public List<PropertyDTO> getProperties() {
        return properties;
    }

    /**
     * Set list of properties.
     *
     * @param properties
     *            list of properties as PropertyDTO
     */
    public void setProperties(List<PropertyDTO> properties) {
        this.properties = properties;
    }

    /**
     * Get process as ProcessDTO.
     * 
     * @return process as ProcessDTO
     */
    public ProcessDTO getProcess() {
        return process;
    }

    /**
     * Set process as ProcessDTO.
     * 
     * @param process
     *            as ProcessDTO
     */
    public void setProcess(ProcessDTO process) {
        this.process = process;
    }
}
