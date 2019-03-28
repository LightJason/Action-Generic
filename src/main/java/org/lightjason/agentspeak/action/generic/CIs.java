/*
 * @cond LICENSE
 * ######################################################################################
 * # LGPL License                                                                       #
 * #                                                                                    #
 * # This file is part of the LightJason AgentSpeak(L++)                                #
 * # Copyright (c) 2015-19, LightJason (info@lightjason.org)                            #
 * # This program is free software: you can redistribute it and/or modify               #
 * # it under the terms of the GNU Lesser General Public License as                     #
 * # published by the Free Software Foundation, either version 3 of the                 #
 * # License, or (at your option) any later version.                                    #
 * #                                                                                    #
 * # This program is distributed in the hope that it will be useful,                    #
 * # but WITHOUT ANY WARRANTY; without even the implied warranty of                     #
 * # MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                      #
 * # GNU Lesser General Public License for more details.                                #
 * #                                                                                    #
 * # You should have received a copy of the GNU Lesser General Public License           #
 * # along with this program. If not, see http://www.gnu.org/licenses/                  #
 * ######################################################################################
 * @endcond
 */

package org.lightjason.agentspeak.action.generic.type;

import org.lightjason.agentspeak.action.IBaseAction;
import org.lightjason.agentspeak.common.IPath;
import org.lightjason.agentspeak.error.context.CExecutionIllegealArgumentException;
import org.lightjason.agentspeak.language.CCommon;
import org.lightjason.agentspeak.language.ITerm;
import org.lightjason.agentspeak.language.execution.IContext;
import org.lightjason.agentspeak.language.fuzzy.IFuzzyValue;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * action to check if a type is a class.
 * The first argument defines a full-qualified Java
 * class name, and all other arguments are checked if
 * that are instances of this class, the action fails
 * if one of the arguments are not an instance of this
 * class or the class does not exist
 *
 * {@code .generic/type( "java.lang.String", X, Y, Z );}
 */
public final class CIs extends IBaseAction
{
    /**
     * serial id
     */
    private static final long serialVersionUID = -8087218847441065975L;
    /**
     * action name
     */
    private static final IPath NAME = namebyclass( CIs.class, "generic", "type" );

    @Nonnull
    @Override
    public IPath name()
    {
        return NAME;
    }

    @Nonnegative
    @Override
    public int minimalArgumentNumber()
    {
        return 2;
    }

    @Nonnull
    @Override
    public Stream<IFuzzyValue<?>> execute( final boolean p_parallel, @Nonnull final IContext p_context,
                                           @Nonnull final List<ITerm> p_argument, @Nonnull final List<ITerm> p_return
    )
    {
        final List<ITerm> l_arguments = CCommon.flatten( p_argument ).collect( Collectors.toList() );
        final Class<?> l_class;
        try
        {
            l_class = Class.forName( l_arguments.get( 0 ).raw() );
        }
        catch ( final ClassNotFoundException l_exception )
        {
            throw new CExecutionIllegealArgumentException( p_context, l_exception );
        }

        return l_arguments.stream()
                          .skip( 1 )
                          .map( ITerm::raw )
                          .allMatch( i -> l_class.isAssignableFrom( i.getClass() ) )
               ? p_context.agent().fuzzy().membership().success()
               : p_context.agent().fuzzy().membership().fail();
    }

}
