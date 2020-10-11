/*
 * Copyright 2016 Albert Tregnaghi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 */
package de.jcup.egradle.codeassist.dsl;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import de.jcup.egradle.core.util.ErrorHandler;

public class PluginMergerTest {

    private static final String TARGET_TYPE = "targetType";
    private static final String TYPE1 = "type1";
    private static final String TYPE2 = "type2";
    private static final String TYPE3 = "type3";
    private static final String TYPE4 = "type4";
    private Set<Plugin> mockedPlugins;
    private TypeProvider mockedProvider;
    private PluginMerger merger;
    private Plugin mockedPlugin1;
    private Plugin mockedPlugin2;
    private Set<TypeExtension> plugin1Extensions;
    private Set<TypeExtension> plugin2Extensions;
    private Type mockedType1;
    private Type mockedType2;
    private Type mockedType3;
    private Type mockedType4;
    private ModifiableType mockedTargetType;
    private ErrorHandler mockedErrorHandler;
    private ReasonImpl reason1;
    private ReasonImpl reason2;

    @Before
    public void before() {
        mockedPlugins = new LinkedHashSet<>();
        mockedProvider = mock(TypeProvider.class);
        mockedErrorHandler = mock(ErrorHandler.class);

        merger = new PluginMerger(mockedProvider, mockedErrorHandler);
        mockedPlugin1 = mock(Plugin.class);
        mockedPlugin2 = mock(Plugin.class);

        reason1 = new ReasonImpl();
        reason2 = new ReasonImpl();
        reason1.setPlugin(mockedPlugin1);
        reason2.setPlugin(mockedPlugin2);

        plugin1Extensions = new LinkedHashSet<>();
        when(mockedPlugin1.getExtensions()).thenReturn(plugin1Extensions);

        plugin2Extensions = new LinkedHashSet<>();
        when(mockedPlugin2.getExtensions()).thenReturn(plugin2Extensions);

        mockedTargetType = mock(ModifiableType.class);
        when(mockedTargetType.getName()).thenReturn(TARGET_TYPE);

        mockedType1 = mock(Type.class);
        mockedType2 = mock(Type.class);
        mockedType3 = mock(Type.class);
        mockedType4 = mock(Type.class);

        when(mockedProvider.getType(TYPE1)).thenReturn(mockedType1);
        when(mockedProvider.getType(TYPE2)).thenReturn(mockedType2);
        when(mockedProvider.getType(TYPE3)).thenReturn(mockedType3);
        when(mockedProvider.getType(TYPE4)).thenReturn(mockedType4);

        mockedPlugins.add(mockedPlugin1);
        mockedPlugins.add(mockedPlugin2);

    }

    @Test
    public void targetType_addExtension_called_when_plugin_is_for_targettype_and_hasExtension_with_id_type1() {
        /* prepare */
        TypeExtension mockedTypeExtension1 = mock(TypeExtension.class);
        when(mockedTypeExtension1.getTargetTypeAsString()).thenReturn(TARGET_TYPE);
        when(mockedTypeExtension1.getExtensionTypeAsString()).thenReturn(TYPE1);
        when(mockedTypeExtension1.getId()).thenReturn("extension1");
        plugin1Extensions.add(mockedTypeExtension1);

        /* execute */
        merger.merge(mockedTargetType, mockedPlugins);

        /* test */
        verify(mockedTargetType).addExtension("extension1", mockedType1, reason1);

    }

    @Test
    public void targetType_addExtension_is_not_called_when_plugin_is_for_targettype_and_hasExtension_with_id_unprovidedType() {
        /* prepare */
        TypeExtension mockedTypeExtension1 = mock(TypeExtension.class);
        when(mockedTypeExtension1.getTargetTypeAsString()).thenReturn(TARGET_TYPE);
        when(mockedTypeExtension1.getExtensionTypeAsString()).thenReturn("unprovidedType");
        when(mockedTypeExtension1.getId()).thenReturn("extension1");
        plugin1Extensions.add(mockedTypeExtension1);

        /* execute */
        merger.merge(mockedTargetType, mockedPlugins);

        /* test */
        verify(mockedTargetType, never()).addExtension(any(String.class), any(Type.class), any(Reason.class));
        verify(mockedErrorHandler).handleError(any(String.class));
    }

    @Test
    public void targetType_addExtension_is_not_called_when_plugin_is_for_other_targettype_and_hasExtension_with_id_type1() {
        /* prepare */
        TypeExtension mockedTypeExtension1 = mock(TypeExtension.class);
        when(mockedTypeExtension1.getTargetTypeAsString()).thenReturn("otherTargetType");
        when(mockedTypeExtension1.getExtensionTypeAsString()).thenReturn(TYPE1);
        when(mockedTypeExtension1.getId()).thenReturn("extension1");
        plugin1Extensions.add(mockedTypeExtension1);

        /* execute */
        merger.merge(mockedTargetType, mockedPlugins);

        /* test */
        verify(mockedTargetType, never()).addExtension(any(String.class), any(Type.class), any(Reason.class));

    }

    @Test
    public void targetType_mixin_type1_called_when_plugin_is_for_targettype_and_mixesIn_type1() {
        /* prepare */
        TypeExtension mockedTypeExtension1 = mock(TypeExtension.class);
        when(mockedTypeExtension1.getTargetTypeAsString()).thenReturn(TARGET_TYPE);
        when(mockedTypeExtension1.getMixinTypeAsString()).thenReturn(TYPE1);
        plugin1Extensions.add(mockedTypeExtension1);

        /* execute */
        merger.merge(mockedTargetType, mockedPlugins);

        /* test */
        verify(mockedTargetType).mixin(mockedType1, reason1);

    }

    @Test
    public void targetType_mixin_type1_is_not_called_when_plugin_is_for_other_targettype_and_mixesIn_type1() {
        /* prepare */
        TypeExtension mockedTypeExtension1 = mock(TypeExtension.class);
        when(mockedTypeExtension1.getTargetTypeAsString()).thenReturn("otherTargetType");
        when(mockedTypeExtension1.getMixinTypeAsString()).thenReturn(TYPE1);
        plugin1Extensions.add(mockedTypeExtension1);

        /* execute */
        merger.merge(mockedTargetType, mockedPlugins);

        /* test */
        verify(mockedTargetType, never()).mixin(mockedType1, reason1);
        verify(mockedErrorHandler, never()).handleError(any(String.class));

    }

    @Test
    public void targetType_mixin_type1_is_not_called_when_plugin_is_for_targettype_but_mixesIn_unprovided_type() {
        /* prepare */
        TypeExtension mockedTypeExtension1 = mock(TypeExtension.class);
        when(mockedTypeExtension1.getTargetTypeAsString()).thenReturn(TARGET_TYPE);
        when(mockedTypeExtension1.getMixinTypeAsString()).thenReturn("unprovidedtype");
        plugin1Extensions.add(mockedTypeExtension1);

        /* execute */
        merger.merge(mockedTargetType, mockedPlugins);

        /* test */
        verify(mockedTargetType, never()).mixin(any(), any(Reason.class));
        verify(mockedErrorHandler).handleError(any(String.class));

    }

    @Test
    public void targetType_mixin_type_multiple_times() {
        /* prepare */
        TypeExtension mockedTypeExtension1 = mock(TypeExtension.class);
        when(mockedTypeExtension1.getTargetTypeAsString()).thenReturn(TARGET_TYPE);
        when(mockedTypeExtension1.getMixinTypeAsString()).thenReturn(TYPE1);
        plugin1Extensions.add(mockedTypeExtension1);

        TypeExtension mockedTypeExtension2 = mock(TypeExtension.class);
        when(mockedTypeExtension2.getTargetTypeAsString()).thenReturn(TARGET_TYPE);
        when(mockedTypeExtension2.getMixinTypeAsString()).thenReturn(TYPE2);
        plugin1Extensions.add(mockedTypeExtension2);

        TypeExtension mockedTypeExtension3 = mock(TypeExtension.class);
        when(mockedTypeExtension3.getTargetTypeAsString()).thenReturn(TARGET_TYPE);
        when(mockedTypeExtension3.getMixinTypeAsString()).thenReturn(TYPE3);
        plugin2Extensions.add(mockedTypeExtension3);

        TypeExtension mockedTypeExtension4 = mock(TypeExtension.class);
        when(mockedTypeExtension4.getTargetTypeAsString()).thenReturn("otherTargetType");
        when(mockedTypeExtension4.getMixinTypeAsString()).thenReturn(TYPE4);
        plugin2Extensions.add(mockedTypeExtension4);

        /* execute */
        merger.merge(mockedTargetType, mockedPlugins);

        /* test */
        verify(mockedTargetType, times(1)).mixin(mockedType1, reason1);
        verify(mockedTargetType, times(1)).mixin(mockedType2, reason1);
        verify(mockedTargetType, times(1)).mixin(mockedType3, reason2);
        verify(mockedTargetType, never()).mixin(mockedType4, reason2);

    }

}
