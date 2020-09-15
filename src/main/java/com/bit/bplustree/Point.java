package com.bit.bplustree;

import jdk.nashorn.internal.objects.annotations.Constructor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author aerfafish
 * @date 2020/9/10 6:08 下午
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Point {

    Comparable key;

    Long value;
}
