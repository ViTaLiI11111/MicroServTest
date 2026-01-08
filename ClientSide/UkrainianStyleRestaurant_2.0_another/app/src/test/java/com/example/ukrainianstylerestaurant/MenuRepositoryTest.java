package com.example.ukrainianstylerestaurant;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.ukrainianstylerestaurant.data.MenuRepository;
import com.example.ukrainianstylerestaurant.model.Category;
import com.example.ukrainianstylerestaurant.net.MenuApi;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class MenuRepositoryTest {
    @Mock
    MenuApi mockApi;

    @Mock
    Call<List<Category>> mockCall;

    MenuRepository repository;

    @Before
    public void setUp(){
        MockitoAnnotations.openMocks(this);
        repository = new MenuRepository(mockApi);
    }

    @Test
    public void getCategories_returnsList_whenApiSuccess() throws IOException {
        //Given
        List<Category> fakeList = new ArrayList<>();
        fakeList.add(new Category(1,"Супи"));
        when(mockApi.getCategories()).thenReturn(mockCall);
        when(mockCall.execute()).thenReturn(Response.success(fakeList));
        //When
        List<Category> result = repository.getCategories();
        //Then
        assertEquals(1, result.size());
        assertEquals("Супи", result.get(0).getTitle());
        verify(mockApi).getCategories();
    }

}
