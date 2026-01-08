package com.example.ukrainianstylerestaurant;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.example.ukrainianstylerestaurant.model.Order;

import org.junit.Before;
import org.junit.Test;

public class OrderTest {
    @Before
    public void SetUp(){
        Order.itemsMap.clear();
    }

    @Test
    public void addToCart_addsNewItem(){
        //Given
        int dishId = 101;
        int qty = 1;
        //When
        Order.itemsMap.put(dishId,qty);
        //Then
        assertTrue(Order.itemsMap.containsKey(dishId));
        assertEquals(Integer.valueOf(1), Order.itemsMap.get(dishId));
    }

    @Test
    public void addToCart_IncrementsExistingItem(){
        //Given
        int dishId = 55;
        Order.itemsMap.put(dishId, 2);
        //When
        int currentQty = Order.itemsMap.getOrDefault(dishId,0);
        Order.itemsMap.put(dishId, currentQty + 1);
        //Then
        assertEquals(Integer.valueOf(3), Order.itemsMap.get(dishId));
    }
}
