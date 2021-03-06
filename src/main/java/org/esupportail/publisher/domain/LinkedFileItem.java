/**
 * Copyright (C) 2014 Esup Portail http://www.esup-portail.org
 * @Author (C) 2012 Julien Gribonvald <julien.gribonvald@recia.fr>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *                 http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.esupportail.publisher.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.esupportail.publisher.domain.util.CstPropertiesLength;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * @author GIP RECIA - Julien Gribonvald  07/04/17.
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@RequiredArgsConstructor
@Entity
@Table(name = "T_LINKEDFILEITEM", uniqueConstraints = @UniqueConstraint(columnNames = {
    "item_id", "uri" }))
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class LinkedFileItem extends AbstractAutoGeneratedIdEntity {

    @NotNull
    @NonNull
    @Size(min = 3, max = CstPropertiesLength.URL)
    @Column(name = "uri", nullable = false)
    private String uri;

    private String filename;

    @NonNull
    @ManyToOne(cascade = {},targetEntity = AbstractItem.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private AbstractItem abstractItem;

    @Column(name = "item_id", insertable = false, updatable = false)
    private Long itemId;

    @Column(length = 1, name = "in_body", nullable = false)
    private boolean inBody = true;

    @Column(length = CstPropertiesLength.CONTENT_TYPE, name = "content_type")
    private String contentType;

    /**
     * Constructor with common properties.
     * @param uri The local uri to find the file
     * @param filename The filename when downloading
     * @param abstractItem Where the file is linked.
     */
    public LinkedFileItem(@NotNull final String uri, final String filename, @NotNull final AbstractItem abstractItem, final boolean inBody, final String contentType) {
        this.uri = uri;
        this.filename = filename;
        this.abstractItem = abstractItem;
        this.inBody = inBody;
        this.contentType = contentType;
    }

    @Override
    public String toString() {
        return "LinkedFileItem(" +
            super.toString() +
            "uri='" + uri + '\'' +
            ", filename='" + filename + '\'' +
            ", item_id=" + itemId + '\'' +
            ", inBody=" + inBody + '\'' +
            ", contentType=" + contentType +
            ')';
    }
}
