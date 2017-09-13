/*
 * Copyright (c) 2017, PCJ Library, Marek Nowicki
 * All rights reserved.
 *
 * Licensed under New BSD License (3-clause license).
 *
 * See the file "LICENSE" for the full license governing this code.
 */
package org.pcj;

import java.io.Serializable;
import java.util.concurrent.Callable;

/**
 *
 * @author faramir
 */
public interface Task<T> extends Callable<T>, Serializable {

}
