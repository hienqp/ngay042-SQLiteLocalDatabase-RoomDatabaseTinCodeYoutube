# 1. Xây Dựng Ứng Dụng Quản Lý User Sử Dụng Room <a id="1"></a>
________________________________________________________________________________________________________________________
- để có thể sử dụng Room API, khai báo nhúng Room API vào file ``build.gradle`` Module ở đường dẫn [https://developer.android.com/training/data-storage/room]("https://developer.android.com/training/data-storage/room")
- các mục optional dành cho những mục đích khác, tùy mục đích sử dụng mà lựa chọn các option tương ứng

```groovy
dependencies {
    def room_version = "2.3.0"

    implementation "androidx.room:room-runtime:$room_version"
    annotationProcessor "androidx.room:room-compiler:$room_version"

    // optional - RxJava2 support for Room
    implementation "androidx.room:room-rxjava2:$room_version"

    // optional - RxJava3 support for Room
    implementation "androidx.room:room-rxjava3:$room_version"

    // optional - Guava support for Room, including Optional and ListenableFuture
    implementation "androidx.room:room-guava:$room_version"

    // optional - Test helpers
    testImplementation "androidx.room:room-testing:$room_version"

    // optional - Paging 3 Integration
    implementation "androidx.room:room-paging:2.4.0-alpha05"
}
```

- để hiển thị danh sách bằng Recyclerview API, khai báo nhúng Recyclerview API vào file ``build.gradle`` Module ở đường dẫn [https://developer.android.com/jetpack/androidx/releases/recyclerview]("https://developer.android.com/jetpack/androidx/releases/recyclerview")

```groovy
dependencies {
    implementation "androidx.recyclerview:recyclerview:1.2.1"
    // For control over item selection of both touch and mouse driven selection
    implementation "androidx.recyclerview:recyclerview-selection:1.1.0"
}
```

## 1.1. Thiết Kế Giao Diện Của Recyclerview <a id="1.1"></a>
________________________________________________________________________________________________________________________
- đầu tiên tạo 1 Layout item ở thư mục __res/layout/__
- đây là giao diện của 1 View (item) trên Recyclerview (danh sách)
- item_user.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    android:padding="10dp">

    <TextView
        android:id="@+id/tv_username"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="@string/app_name"
        android:textColor="@color/black"
        android:textSize="16sp" />

    <TextView
        android:id="@+id/tv_address"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="3dp"
        android:text="@string/app_name"
        android:textColor="@color/gray"
        android:textSize="14sp" />
</LinearLayout>
```

## 1.2. Tạo Class User.java <a id="1.2"></a>
________________________________________________________________________________________________________________________
- User.java là 1 model class để tạo ra các object user
- User.java
```java
package com.hienqp.roomdatabasetincodeyoutube;

public class User {
    private String userName;
    private String address;

    public User(String username, String address) {
        this.username = username;
        this.address = address;
    }

    public String getUsername() {
      return username;
    }
    
    public void setUsername(String username) {
      this.username = username;
    }
    
    public String getAddress() {
      return address;
    }
    
    public void setAddress(String address) {
      this.address = address;
    }
}
```

## 1.3. Tạo Class UserAdapter.java <a id="1.3"></a>
________________________________________________________________________________________________________________________
- xây dựng UserAdapter là 1 Adapter để thao tác giữa DataSource và Recyclerview
- trong UserAdapter xây dựng 1 inner class UserViewHolder làm ViewHolder, inner class này extends Recyclerview.ViewHolder
  - sau khi extends Recyclerview.ViewHolder ta Alt+insert 1 constructor mặc định cho inner class UserViewHolder
  - khai báo các field có trong item_user.xml
  - trong constructor của inner class UserViewHolder tiến hành ánh xạ các field vừa khai báo thông qua View được truyền 
  vào constructor
- sau khi xây dựng xong inner class ViewHolder, ta thêm khai báo extends Recyclerview.Adapter<> cho UserAdapter
  - truyền kiểu dữ liệu cho Adapter là ViewHolder đã thiết kế ``RecyclerView.Adapter<UserAdapter.UserViewHolder>``
  - sau khi extends RecyclerView.Adapter<UserAdapter.UserViewHolder>, tiến hành implements 3 method của Recyclerview
    - onCreateViewHolder()
    - onBindViewHolder()
    - getItemCount()
  - khai báo field List<User> cho UserAdapter
  - khai báo method setData(List<User>) cho UserAdapter, đối số truyền vào là List<User> vừa khai báo
  - xử lý method getItemCount(): kiểm tra 
    - nếu List<User> khác null thì return size() của List<User>
    - nếu bằng null thì return 0
  - xử lý method onCreateViewHolder()
    - tạo 1 object View inflate() từ Layout item_user.xml: 
      - ``View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);``
    - sau khi có object View, return 1 anonymous object UserViewHolder với tham số truyền vào là object View vừa tạo
      - ``return new UserViewHolder(view);``
  - xử lý method set data lên Recyclerview: onBindViewHolder()
    - lấy ra object User từ List<User> ở position được truyền vào onBindViewHolder
      - ``User user = mListUser.get(position);``
    - kiểm tra object User:
      - nếu bằng null thì chỉ return mà không làm gì
      - ngược lại thì set data cho UserViewHolder tương ứng với data của object User có được khác null
- UserAdapter.java
```java
package com.hienqp.roomdatabasetincodeyoutube;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder>{
  private List<User> mListUser;

  public void setData(List<User> list) {
    UserAdapter.this.mListUser = list;
    notifyDataSetChanged();
  }

  @NonNull
  @Override
  public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
    return new UserViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
    User user = mListUser.get(position);
    if (user == null) {
      return;
    }

    holder.tvUsername.setText(user.getUsername());
    holder.tvAddress.setText(user.getAddress());
  }

  @Override
  public int getItemCount() {
    if (mListUser != null) {
      return mListUser.size();
    }
    return 0;
  }

  public class UserViewHolder extends RecyclerView.ViewHolder {
    private TextView tvUsername;
    private TextView tvAddress;

    public UserViewHolder(@NonNull View itemView) {
      super(itemView);

      tvUsername = (TextView) itemView.findViewById(R.id.tv_username);
      tvAddress = (TextView) itemView.findViewById(R.id.tv_address);
    }
  }
}
```

## 1.4. Thao Tác Với Room API <a id="1.4"></a>
________________________________________________________________________________________________________________________
- Room có 3 thành phần chính
  - Entity (thực thể): 
    - đại diện cho 1 Table trong database
    - trong Table gồm các Column chính là các Field của object cần quản lý
  - Database
    - thực hiện truy vấn database
    - trả về kết quả từ truy vấn cho người dùng
  - DAO (Database Access Object):
    - là 1 đối tượng truy cập database
    - chứa những method để truy cập vào database
      - Query
      - Insert
      - Update
      - Delete

### 1.4.1. Thiết Lập Entity User <a id="1.4.1"></a>
________________________________________________________________________________________________________________________
- tiến hành cài đặt class Entity User
- trong User.java
  - khai báo annotation @Entity trước dòng khai báo class User
  - thêm cặp () để thêm thuộc tính cho annotation @Entity
  - trong cặp () nhấn Ctrl+space, và thêm thuộc tính tableName và chỉ định String là tên của Table trong database
    - ``@Entity(tableName = "user")``
  - 1 Table phải có ít nhất 1 Primary Key
    - khai báo field id: 
    - thêm annotation @PrimaryKey cho field id, và set thuộc tính cho annotation này là tự động tạo giá trị
      - ``@PrimaryKey(autoGenerate = true)``
    - thêm method Getter và Setter cho field id
  - thêm annotation @ColumInfo và chỉ định tên của Column (field) trong Table trước các field: id, username, address 
  (có thể bỏ qua bước này, tên Column sẽ là tên của field)
    - ``@ColumnInfo(name = "id")``
    - ``@ColumnInfo(name = "username")``
    - ``@ColumnInfo(name = "address")``

### 1.4.2. Khai Báo Interface DAO <a id="1.4.2"></a>
________________________________________________________________________________________________________________________
- thiết kế 1 interface DAO chứa những method abstract để truy cập database (DAO phải là 1 interface)
  - Query
  - Insert
  - Update
  - Delete
  - ...
- đầu tiên DAO của ta cần thiết kế có 2 method là Query ra 1 List<User> và 1 method Insert 1 User vào database
- khai báo annotation @Dao trước khai báo interface DAO để Room biết được đây là 1 DAO
- trong DAO ta khai báo các method trừu tượng theo mục đích cụ thể, và khai báo annotation tương ứng trước mỗi method
  - @Insert là annotation cho method insert User vào database
  - @Query là annotation cho method query và lấy ra dữ liệu trong database
    - ở annotation @Query ta khai báo thêm chuỗi SQL tùy vào mục đích cụ thể của method đi kèm @Query
- interface UserDAO.java
```java
package com.hienqp.roomdatabasetincodeyoutube.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.hienqp.roomdatabasetincodeyoutube.User;

import java.util.List;

@Dao
public interface UserDAO {
    @Insert
    void insertUser(User user);

    @Query("SELECT * FROM user")
    List<User> getListUser();
}
```

### 1.4.3. Khai Báo Abstract Class Database Extends Từ RoomDatabase <a id="1.4.3"></a>
________________________________________________________________________________________________________________________
- thành phần @Database là nơi truy cập chính đến database
- nó phải được khai báo là abstract class và extends từ RoomDatabase
- trong suốt quá trình ứng dụng làm việc, chỉ cần 1 Database vì vậy thành phần này nên được thiết kế theo Singleton design pattern
- abstract class này phải được gắn annotation @Database đi kèm với 2 thuộc tính:
  - entities : mảng danh sách các data entities liên kết với database
  - version : chỉ định phiên bản cho database (bắt đầu bằng 1)
- khai báo field final DATABASE_NAME cho Database
- khai báo field là instance của abstract class này (vì thiết kế theo Singleton)
- khai báo 1 method synchronized trả về instance của thành phần này
- với mỗi @Dao liên kết với @Database ta phải khai báo 1 abstract method trả về instance của @Dao tương ứng
- UserDatabase.java
```java
package com.hienqp.roomdatabasetincodeyoutube.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.hienqp.roomdatabasetincodeyoutube.User;

@Database(entities = {User.class}, version = 1)
public abstract class UserDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "user.db";
    private static UserDatabase instanceUserDatabase;

    public static synchronized UserDatabase getInstance(Context context) {
        if (instanceUserDatabase == null) {
            instanceUserDatabase = Room.databaseBuilder(context.getApplicationContext(), UserDatabase.class, DATABASE_NAME)
                    .allowMainThreadQueries() // cho phép setting Query trên MainThread
                    .build();
        }

        return instanceUserDatabase;
    }

    public abstract UserDAO userDAO();
}
```

## 1.5. MainActivity <a id="1.5"></a>
________________________________________________________________________________________________________________________
- ở MainActivity tiến hành xử lý giao diện, logic, và áp dụng Room đã xây dựng cho ứng dụng

### 1.5.1. activity_main.xml - Thiết Kế Giao Diện <a id="1.5.1"></a>
________________________________________________________________________________________________________________________
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="16dp"
    tools:context=".MainActivity">

    <EditText
        android:id="@+id/edt_username"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:hint="Username"
        android:textColor="@color/black"
        android:textSize="16sp" />

    <EditText
        android:id="@+id/edt_address"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="14dp"
        android:hint="Address"
        android:textColor="@color/black"
        android:textSize="16sp" />

    <Button
        android:id="@+id/btn_add_user"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="20dp"
        android:background="@color/design_default_color_secondary_variant"
        android:text="Add User"
        android:textColor="@color/white"
        android:textSize="16sp" />

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/rcv_user"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_marginTop="20dp" />

</LinearLayout>
```

### 1.5.2. MainActivity.java - Xử Lý Logic Sự Kiện <a id="1.5.2"></a>
________________________________________________________________________________________________________________________
- ở MainActivity
  - khởi tạo các thành phần UI
  - khởi tạo Adapter
  - khởi tạo DataSource
  - khai báo method add User
  - khai báo method hideSoftKeyboard (ẩn bàn phím ảo của thiết bị)
- MainActivity.java
```java
package com.hienqp.roomdatabasetincodeyoutube;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hienqp.roomdatabasetincodeyoutube.database.UserDatabase;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
  private EditText edtUsername;
  private EditText edtAddress;
  private Button btnAddUser;
  private RecyclerView rcvUser;

  private UserAdapter userAdapter;
  private List<User> mListUser;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    initUi();

    userAdapter = new UserAdapter();
    mListUser = new ArrayList<>();
    userAdapter.setData(mListUser);

    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this);
    rcvUser.setLayoutManager(linearLayoutManager);

    rcvUser.setAdapter(userAdapter);
    loadData();

    btnAddUser.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        addUser();
      }
    });
  }

  private void initUi() {
    edtUsername = (EditText) findViewById(R.id.edt_username);
    edtAddress = (EditText) findViewById(R.id.edt_address);
    btnAddUser = (Button) findViewById(R.id.btn_add_user);
    rcvUser = (RecyclerView) findViewById(R.id.rcv_user);
  }

  private void addUser() {
    String strUsername = edtUsername.getText().toString().trim();
    String strAddress = edtAddress.getText().toString().trim();

    if (TextUtils.isEmpty(strUsername) || TextUtils.isEmpty(strAddress)) {
      return;
    }

    User user = new User(strUsername, strAddress);

    UserDatabase.getInstance(MainActivity.this).userDAO().insertUser(user);
    notifyToast(MainActivity.this, "Add user successfully");

    edtUsername.setText("");
    edtAddress.setText("");

    hideSoftKeyboard();

    loadData();
  }

  private void loadData() {
    mListUser =  UserDatabase.getInstance(MainActivity.this).userDAO().getListUser();
    userAdapter.setData(mListUser);
  }

  private void hideSoftKeyboard() {
    try {
      InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
      inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    } catch (NullPointerException ex) {
      ex.printStackTrace();
    }
  }

  private void notifyToast(Context context, String message) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
  }
}
```

## 1.6. Chức Năng Kiểm Tra Record Trong Database Và Update Record Trong Database <a id="1.6"></a>
________________________________________________________________________________________________________________________
- xây dựng tính năng:
  - kiểm tra data input có trùng với record trong database hay không
  - update chỉnh sửa 1 record trong database

### 1.6.1. Chức Năng Kiểm Tra Record Trong Database <a id="1.6.1"></a>
________________________________________________________________________________________________________________________
- trong @Dao khai báo 1 method kiểm tra User đã tồn tại trong database hay chưa
- thêm vào @Dao method checkUser(String) như sau
```java
    @Query("SELECT * FROM user WHERE username= :username")
    List<User> checkUser(String username);
```
- trong MainActivity.java khai báo method isUserExist(User) để kiểm tra User đã tồn tại trong database hay chưa
```java
    private boolean isUserExist(User user) {
        List<User> list = UserDatabase.getInstance(MainActivity.this).userDAO().checkUser(user.getUsername());
        return list != null && !list.isEmpty();
    }
```
- trong method addUser() của MainActivity, thêm dòng lệnh kiểm tra User đã tồn tại hay chưa, trước khi insertUser vào database
```java
        if (isUserExist(user)) {
            notifyToast(MainActivity.this, "User exist");
            return;
        }
```

### 1.6.2. Chức Năng Update Record Trong Database <a id="1.6.2"></a>
________________________________________________________________________________________________________________________
- chỉnh sửa lại UI của View (item) trên Recyclerview
- ở item_user.xml ta thêm Button dùng để gọi chức năng update record trong database
- item_user.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:padding="10dp">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_centerVertical="true"
        android:layout_marginEnd="10dp"
        android:layout_toStartOf="@id/btn_update"
        android:orientation="vertical">

        <TextView
            android:id="@+id/tv_username"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="@string/app_name"
            android:textColor="@color/black"
            android:textSize="16sp" />

        <TextView
            android:id="@+id/tv_address"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="3dp"
            android:text="@string/app_name"
            android:textColor="@color/gray"
            android:textSize="14sp" />
    </LinearLayout>

    <Button
        android:id="@+id/btn_update"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_alignParentEnd="true"
        android:layout_centerVertical="true"
        android:text="Update" />

</RelativeLayout>
```
- xử lý logic ở UserAdapter.java
  - trong inner class UserViewHolder tiến hành khai báo ánh xạ thêm 1 Button update
  - inner class UserViewHolder
```java
    public class UserViewHolder extends RecyclerView.ViewHolder {
        private TextView tvUsername;
        private TextView tvAddress;
        
        // khai báo thêm Button update
        private Button btnUpdate;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            tvUsername = (TextView) itemView.findViewById(R.id.tv_username);
            tvAddress = (TextView) itemView.findViewById(R.id.tv_address);
            
            // ánh xạ Button update
            btnUpdate = (Button) itemView.findViewById(R.id.btn_update); 
        }
    }
```
- khi ta nhấn Button Update
  - chúng ta sẽ truy vấn database
  - load lại Recyclerview
- 2 hành động trên ta cần thực hiện trên MainActivity
- tuy nhiên sự kiện xảy ra lại trên Adapter, nên trong UserAdapter ta sẽ khai báo 1 interface để callback sự kiện ra bên ngoài
- interface IClickItemUser
```java
// khai báo field
private IClickItemUser iClickItemUser;

// khai báo constructor với đối số kiểu interface vừa khai báo
public UserAdapter(IClickItemUser iClickItemUser) {
    this.iClickItemUser = iClickItemUser;
}

// khai báo interface đi kèm với method để callback ra bên ngoài sự kiện update
public interface IClickItemUser {
    void updateUser(User user);
}
```
- sau đó trong onBindViewHolder() của UserAdapter ta set sự kiện Button Update của ViewHolder, tham số truyền vào là 1 anonymous
object interface vừa khai báo, trong sự kiện ta gọi method của interface và truyền vào tham số cần thiết là User mà UserAdapter đang
quản lý
```java
holder.btnUpdate.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        iClickItemUser.updateUser(user);
    }
});
```
- quay trở lại MainActivity, trong onCreate(), lệnh khởi tạo UserAdapter sẽ báo lỗi, vì trong UserAdapter ta đã khai báo
1 constructor với tham số truyền vào là interface callback, tiến hành chỉnh sửa truyền vào 1 anonymous object interface callback
- lúc này nó sẽ tự động override method của interface callback, và truyền cho ta 1 tham số cần thiết User để xử lý logic
- sau khi có tham số User mà UserAdapter quản lý truyền qua MainActivity, tiến hành sử dụng tham số này, gọi đến method
xử lý logic updateUser mà ta sẽ cài đặt
```java
userAdapter = new UserAdapter(new UserAdapter.IClickItemUser() {
    @Override
    public void updateUser(User user) {
        clickUpdateUser(user);
    }
});
```
- khai báo logic của method clickUpdateUser(User)
```java
private void clickUpdateUser(User user) {
    //
}
```
- trước khi xử lý logic ở method clickUpdateUser(User), ta cần xây dựng UI cũng như Logic 1 Activity hiển thị nội dung update record
- ở thư mục Project ta tạo 1 Empty Activity bao gồm file source code và file layout với tên Activity là UpdateActivity
- thiết kế UI cho UpdateActivity
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="16dp"
    tools:context=".MainActivity">

    <EditText
        android:id="@+id/edt_username"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:hint="Username"
        android:textColor="@color/black"
        android:textSize="16sp" />

    <EditText
        android:id="@+id/edt_address"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="14dp"
        android:hint="Address"
        android:textColor="@color/black"
        android:textSize="16sp" />

    <Button
        android:id="@+id/btn_update_user"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="20dp"
        android:background="@color/design_default_color_secondary_variant"
        android:text="Update User"
        android:textColor="@color/white"
        android:textSize="16sp" />

</LinearLayout>
```
- khai báo ánh xạ các View ở UpdateActivity.java
```java
package com.hienqp.roomdatabasetincodeyoutube;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class UpdateActivity extends AppCompatActivity {
    private EditText edtUsername;
    private EditText edtAddress;
    private Button btnUpdateUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        edtUsername = (EditText) findViewById(R.id.edt_username);
        edtAddress = (EditText) findViewById(R.id.edt_address);
        btnUpdateUser = (Button) findViewById(R.id.btn_update_user);
    }

    private void notifyToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
```
- sử dụng Intent để truyền đối tượng User từ MainActivity sang UpdateActivity
- lúc này ta tiến hành cài đặt cho method clickUpdateUser(User)
  - khai báo 1 Intent truyền từ MainActivity.this sang UpdateActivity.class
  - khai báo Bundle để truyền gói đối tượng
  - sử dụng method putSerializable(String, Object) của Bundle (lưu ý: tham số Object này phải implements Serializable)
  - sau khi Bundle đã có dữ liệu, ta truyền Bundle vào Intent với method putExtras(Bundle) của Intent
  - sau khi Intent có dữ liệu ta tiến hành kích hoạt Intent với method startActivityForResult(Intent, int)
- method clickUpdateUser(User)
```java
    private void clickUpdateUser(User user) {
        Intent intent = new Intent(MainActivity.this, UpdateActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("object_user", user);
        intent.putExtras(bundle);
        startActivityForResult(intent, MY_REQUEST_CODE);
    }
```
- tiến hành nhận dữ liệu từ Intent bên UpdateActivity trong onCreate()
  - khai báo 1 field User của UpdateActivity: ``private User mUser;``
  - trong onCreate() get dữ liệu trong Intent truyền từ MainActivity qua với đúng REQUEST_CODE của Intent tương ứng
  - vì kiểu dữ liệu trả về là Serializable nên lệnh get dữ liệu phải ép kiểu về User
  - sau khi có được User, thực hiện show giá trị tương ứng của User nhận được lên màn hình nếu User nhận được khác null
```java
mUser = (User) getIntent().getExtras().get("object_user");

if (mUser != null) {
    edtUsername.setText(mUser.getUsername());
    edtAddress.setText(mUser.getAddress());
}
```
- sau khi show giá trị của User nhận được lên màn hình, nếu người dùng chỉnh sửa lại và nhấn Button update, ta tiến hành
bắt sự kiện này
- tiếp tục trong onCreate() của UpdateActivity, bắt sự kiện của Button update khi người dùng chỉnh sửa record xong, ta gọi
method update record này trong database
```java
btnUpdateUser.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        updateUser();
    }
});
```
- xây dựng method updateUser() trong UpdateActivity
  - logic trong method này là
    - người dùng sẽ chỉnh sửa trên 2 EditText
    - ta phải lấy giá trị của 2 EditText này
    - kiểm tra nếu giá trị 1 trong 2 không empty ta sẽ tiếp tục update dữ liệu này vào database
- trước khi tiến hành update nếu dữ liệu không empty, ta xây dựng method update trong @Dao
```java
@Update
void updateUser(User user);
```
- sau khi xây dựng method update trong @Dao, tiến hành sử dụng method đó update đối tượng User đang làm việc vào database
- sau khi update thành công, ta setResult(Activity.RESULT_OK, Intent) trả kết quả về cho MainActivity
```java
    private void updateUser() {
        String strUsername = edtUsername.getText().toString().trim();
        String strAddress = edtAddress.getText().toString().trim();

        if (TextUtils.isEmpty(strUsername) || TextUtils.isEmpty(strAddress)) {
            return;
        }

        // Update User
        mUser.setUsername(strUsername);
        mUser.setAddress(strAddress);

        UserDatabase.getInstance(UpdateActivity.this).userDAO().updateUser(mUser);
        notifyToast(UpdateActivity.this, "Update user successfully");

        Intent intentResult = new Intent();
        setResult(Activity.RESULT_OK, intentResult);
        finish();
    }
```
- quay trở lại MainActivity, để nhận được kết quả trả về từ UpdateActivity, ta phải override lại method bắt sự kiện nếu
có 1 Intent Result trả về: ``onActivityResult(int, int, Intent)``
  - tham số int đầu tiên: là REQUEST gửi đi (MY_REQUEST_CODE)
  - tham số int thứ hai: là RESULT trả về (Acitivity.RESULT_OK)
  - tham số Intent thứ ba: là Intent trả về
- trong onActivityResult() ta kiểm tra
  - requestCode truyền vào đúng bằng REQUEST_CODE gửi đi
  - resultCode truyền vào đúng bằng RESULT_CODE trả về
- nếu thỏa cả 2 điều kiện trên nghĩa là thao tác update bên UpdateActivity đã thành công, database đã update data mới
- tiến hành load lại Recyclerview
- method onActivityResult()
```java
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MY_REQUEST_CODE && resultCode == Activity.RESULT_OK ) {
            loadData();
        }
    }
```

## 1.7. Tìm Kiếm, Xóa 1 Hoặc Tất Cả Record Trong Database <a id="1.7"></a>
________________________________________________________________________________________________________________________
### 1.7.1. Xóa 1 Record Trong Database <a id="1.7.1"></a>
________________________________________________________________________________________________________________________
#### Chỉnh Sửa UI Của Item - Thêm 1 Button Delete
________________________________________________________________________________________________________________________
- trong item_user.xml ta thiết kế 1 Button dùng để Delete item chỉ định
```xml
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:padding="10dp">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_centerVertical="true"
        android:layout_marginEnd="10dp"
        android:layout_toStartOf="@id/btn_update"
        android:orientation="vertical">

        <TextView
            android:id="@+id/tv_username"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="@string/app_name"
            android:textColor="@color/black"
            android:textSize="16sp" />

        <TextView
            android:id="@+id/tv_address"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="3dp"
            android:text="@string/app_name"
            android:textColor="@color/gray"
            android:textSize="14sp" />
    </LinearLayout>
<!--Chỉnh sửa lại Button Update-->
    <Button
        android:id="@+id/btn_update"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_centerVertical="true"
        android:layout_marginEnd="5dp"
        android:layout_toStartOf="@id/btn_delete"
        android:text="Update" />
<!--thêm Button Delete-->
    <Button
        android:id="@+id/btn_delete"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_alignParentEnd="true"
        android:layout_centerVertical="true"
        android:text="Delete" />

</RelativeLayout>
```
#### Bắt Sự Kiện Delete Trong UserAdapter
________________________________________________________________________________________________________________________
- về mặt UI thì người dùng thao tác nhấn Button Delete trên MainActivity, sẽ truy vấn database, và thực hiện xóa record
đã chọn delete trong database
- nhưng về mặt sự kiện lại xảy ra ở UserAdapter, nên ta tiến hành cài đặt logic bắt sự kiện delete trong UserAdapter
- trong UserAdapter
  - khai báo ánh xạ Button Delete đã thiết kế ở item_user.xml trong inner class ViewHolder của UserAdapter
  - cài đặt bắt sự kiện của Button Delete trong method onBindViewHolder()
    - trong interface dùng để callback method với các tham số truyền ra bên ngoài ta thêm method abstract deleteUser()
    - trong method bắt sự kiện Button Delete, gọi method delete thông qua interface callback
```java
package com.hienqp.roomdatabasetincodeyoutube;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder>{
  private List<User> mListUser;
  private IClickItemUser iClickItemUser;

  public UserAdapter(IClickItemUser iClickItemUser) {
    this.iClickItemUser = iClickItemUser;
  }

  public void setData(List<User> list) {
    UserAdapter.this.mListUser = list;
    notifyDataSetChanged();
  }

  public interface IClickItemUser {
    void updateUser(User user);
    
    // method delete user
    void deleteUser(User user);
  }

  @NonNull
  @Override
  public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
    return new UserViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
    final User user = mListUser.get(position);
    if (user == null) {
      return;
    }

    holder.tvUsername.setText(user.getUsername());
    holder.tvAddress.setText(user.getAddress());
    holder.btnUpdate.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        iClickItemUser.updateUser(user);
      }
    });
    
    // bắt sự kiện cho Button Delete
    holder.btnDelete.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
          // callback method kèm tham số ra bên ngoài
        iClickItemUser.deleteUser(user);
      }
    });
  }

  @Override
  public int getItemCount() {
    if (mListUser != null) {
      return mListUser.size();
    }
    return 0;
  }

  public class UserViewHolder extends RecyclerView.ViewHolder {
    private TextView tvUsername;
    private TextView tvAddress;
    private Button btnUpdate;
    
    // khai báo view
    private Button btnDelete;

    public UserViewHolder(@NonNull View itemView) {
      super(itemView);

      tvUsername = (TextView) itemView.findViewById(R.id.tv_username);
      tvAddress = (TextView) itemView.findViewById(R.id.tv_address);
      btnUpdate = (Button) itemView.findViewById(R.id.btn_update);
      
      // ánh xạ view
      btnDelete = (Button) itemView.findViewById(R.id.btn_delete);
    }
  }
}
```

#### Xử Lý Logic Ở MainActivity Và UserDAO
________________________________________________________________________________________________________________________
- khi interface callback trong UserAdapter khai báo thêm method abstract deleteUser(User) thì ở MainActivity sẽ báo lỗi
- vì ban đầu khi khởi tạo UserAdapter ta chỉ truyền vào 1 anonymous object của interface callback và chỉ implements 1 method update
- bây giờ interface callback có thêm method deleteUser(), trong lệnh khởi tạo UserAdapter ta chỉ cần implements thêm method deleteUser()
- trong method override lại method của interface callback, ta gọi đến 1 method (tự xây dựng) bên ngoài để xử lý sự kiện delete
- ở method (tự xây dựng) bên ngoài để delete user: clickDeleteUser(User) method
  - ta xây dựng 1 AlertDialog của android.app với Title, Message, Positive Button, Negative Button
  - ở Positive Button sẽ là nơi thực hiện logic delete user trong database
- trước khi thực hiện logic delete user trong database ở Positive Button, ta vào @Dao khai báo thêm method delete 1 User
- UserDAO.java
```java
package com.hienqp.roomdatabasetincodeyoutube.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.hienqp.roomdatabasetincodeyoutube.User;

import java.util.List;

@Dao
public interface UserDAO {
    @Insert
    void insertUser(User user);

    @Query("SELECT * FROM user")
    List<User> getListUser();

    @Query("SELECT * FROM user WHERE username= :username")
    List<User> checkUser(String username);

    @Update
    void updateUser(User user);

    // method delete 1 user
    @Delete
    void deleteUser(User user);
}
```
- quay trở lại MainActivity ta xử lý logic ở Posititve Button sẽ delete user được chỉ định
```java
package com.hienqp.roomdatabasetincodeyoutube;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hienqp.roomdatabasetincodeyoutube.database.UserDatabase;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int MY_REQUEST_CODE = 10;
    private EditText edtUsername;
    private EditText edtAddress;
    private Button btnAddUser;
    private RecyclerView rcvUser;

    private UserAdapter userAdapter;
    private List<User> mListUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUi();

        userAdapter = new UserAdapter(new UserAdapter.IClickItemUser() {
            @Override
            public void updateUser(User user) {
                clickUpdateUser(user);
            }
            
            // override thêm method delete user của interface callback
            @Override
            public void deleteUser(User user) {
                // gọi method bên ngoài để delete user chỉ định
                clickDeleteUser(user);
            }
        });
        mListUser = new ArrayList<>();
        userAdapter.setData(mListUser);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this);
        rcvUser.setLayoutManager(linearLayoutManager);

        rcvUser.setAdapter(userAdapter);
        loadData();

        btnAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addUser();
            }
        });
    }

    // method riêng bên ngoài xử lý logic delete user trong database
    private void clickDeleteUser(User user) {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Confirm Delete User")
                .setMessage("Are you sure ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // logic delete user trong database
                        UserDatabase.getInstance(MainActivity.this).userDAO().deleteUser(user);
                        notifyToast(MainActivity.this, "Delete user successfully");

                        loadData();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void clickUpdateUser(User user) {
        Intent intent = new Intent(MainActivity.this, UpdateActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("object_user", user);
        intent.putExtras(bundle);
        startActivityForResult(intent, MY_REQUEST_CODE);
    }

    private void initUi() {
        edtUsername = (EditText) findViewById(R.id.edt_username);
        edtAddress = (EditText) findViewById(R.id.edt_address);
        btnAddUser = (Button) findViewById(R.id.btn_add_user);
        rcvUser = (RecyclerView) findViewById(R.id.rcv_user);
    }

    private void addUser() {
        String strUsername = edtUsername.getText().toString().trim();
        String strAddress = edtAddress.getText().toString().trim();

        if (TextUtils.isEmpty(strUsername) || TextUtils.isEmpty(strAddress)) {
            return;
        }

        User user = new User(strUsername, strAddress);

        if (isUserExist(user)) {
            notifyToast(MainActivity.this, "User exist");
            return;
        }

        UserDatabase.getInstance(MainActivity.this).userDAO().insertUser(user);
        notifyToast(MainActivity.this, "Add user successfully");

        edtUsername.setText("");
        edtAddress.setText("");

        hideSoftKeyboard();

        loadData();
    }

    private void loadData() {
        mListUser =  UserDatabase.getInstance(MainActivity.this).userDAO().getListUser();
        userAdapter.setData(mListUser);
    }

    private boolean isUserExist(User user) {
        List<User> list = UserDatabase.getInstance(MainActivity.this).userDAO().checkUser(user.getUsername());
        return list != null && !list.isEmpty();
    }

    private void hideSoftKeyboard() {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    private void notifyToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MY_REQUEST_CODE && resultCode == Activity.RESULT_OK ) {
            loadData();
        }
    }
}
```


### 1.7.2. Xóa Tất Cả Record Trong Database <a id="1.7.2"></a>
________________________________________________________________________________________________________________________
#### Chỉnh Sửa UI Của MainActivity - Thêm 1 View Chọn Chức Năng Xóa Tất Cả
________________________________________________________________________________________________________________________
- trong activity_main.xml ta thêm 1 TextView, để khi người dùng click vào sẽ xóa tất cả Record trong Database
- activity_main.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
              xmlns:tools="http://schemas.android.com/tools"
              android:layout_width="match_parent"
              android:layout_height="match_parent"
              android:orientation="vertical"
              android:padding="16dp"
              tools:context=".MainActivity">

  <EditText
          android:id="@+id/edt_username"
          android:layout_width="match_parent"
          android:layout_height="wrap_content"
          android:hint="Username"
          android:textColor="@color/black"
          android:textSize="16sp" />

  <EditText
          android:id="@+id/edt_address"
          android:layout_width="match_parent"
          android:layout_height="wrap_content"
          android:layout_marginTop="14dp"
          android:hint="Address"
          android:textColor="@color/black"
          android:textSize="16sp" />

  <Button
          android:id="@+id/btn_add_user"
          android:layout_width="match_parent"
          android:layout_height="wrap_content"
          android:layout_marginTop="20dp"
          android:background="@color/design_default_color_secondary_variant"
          android:text="Add User"
          android:textColor="@color/white"
          android:textSize="16sp" />

<!--UI chức năng click xóa tất cả các record có trong database-->
  <TextView
          android:id="@+id/tv_delete_all"
          android:layout_width="wrap_content"
          android:layout_height="wrap_content"
          android:layout_gravity="end"
          android:layout_margin="10dp"
          android:text="Delete All"
          android:textColor="@color/teal_700"
          android:textSize="20sp" />

  <androidx.recyclerview.widget.RecyclerView
          android:id="@+id/rcv_user"
          android:layout_width="match_parent"
          android:layout_height="match_parent"
          android:layout_marginTop="20dp" />

</LinearLayout>
```

#### UserDAO - Thêm Method Trừu Tượng Xóa Tất Cả Bản Ghi Trong Table
________________________________________________________________________________________________________________________
```java
package com.hienqp.roomdatabasetincodeyoutube.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.hienqp.roomdatabasetincodeyoutube.User;

import java.util.List;

@Dao
public interface UserDAO {
    @Insert
    void insertUser(User user);

    @Query("SELECT * FROM user")
    List<User> getListUser();

    @Query("SELECT * FROM user WHERE username= :username")
    List<User> checkUser(String username);

    @Update
    void updateUser(User user);

    @Delete
    void deleteUser(User user);

    // method xóa tất cả bản ghi trong table kèm với annotation và câu truy vấn SQL
    @Query("DELETE FROM user")
    void deleteAllUser();
}
```

#### MainActivity.java
________________________________________________________________________________________________________________________
- sau khi thiết kế UI cho MainAcitivity, thêm 1 View click xóa tất cả record có trong database, và bổ sung thêm method
xóa tất cả bản ghi trong table
- quay trở lại MainActivity.java, ta tiến hành
  - khai báo ánh xạ View click chức năng xóa tất cả bản ghi trong table
  - khai báo sự kiện cho View vừa khai báo ánh xạ, khi click vào sẽ gọi đến method (tự xây dựng) bên ngoài truy cập database và xóa dữ liệu
  - ở method (tự xây dựng) bên ngoài, thực hiện
    - show Dialog thông báo hỏi người dùng xác nhận hành động
    - xây dựng logic xóa các bản ghi nếu người dùng đồng ý
- MainActivity.java
```java
package com.hienqp.roomdatabasetincodeyoutube;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hienqp.roomdatabasetincodeyoutube.database.UserDatabase;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int MY_REQUEST_CODE = 10;
    private EditText edtUsername;
    private EditText edtAddress;
    private Button btnAddUser;
    private RecyclerView rcvUser;
    
    // khai báo View chức năng xóa tất cả record
    private TextView tvDeleteAll;

    private UserAdapter userAdapter;
    private List<User> mListUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUi();

        userAdapter = new UserAdapter(new UserAdapter.IClickItemUser() {
            @Override
            public void updateUser(User user) {
                clickUpdateUser(user);
            }

            @Override
            public void deleteUser(User user) {
                clickDeleteUser(user);
            }
        });
        mListUser = new ArrayList<>();
        userAdapter.setData(mListUser);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this);
        rcvUser.setLayoutManager(linearLayoutManager);

        rcvUser.setAdapter(userAdapter);
        loadData();

        btnAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addUser();
            }
        });

        // bắt sự kiện người dùng click vào chọn xóa tất cả bản ghi
        tvDeleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // gọi đến method thực hiện xóa tất cả bản ghi
                clickDeleteAllUser();
            }
        });
    }

    // method tự xây dựng xóa tất cả record trong table của database
    private void clickDeleteAllUser() {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Confirm Delete All User")
                .setMessage("Are you sure ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // logic delete all user trong database
                        UserDatabase.getInstance(MainActivity.this).userDAO().deleteAllUser();
                        notifyToast(MainActivity.this, "Delete all user successfully");

                        loadData();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void clickDeleteUser(User user) {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Confirm Delete User")
                .setMessage("Are you sure ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // logic delete user trong database
                        UserDatabase.getInstance(MainActivity.this).userDAO().deleteUser(user);
                        notifyToast(MainActivity.this, "Delete user successfully");

                        loadData();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void clickUpdateUser(User user) {
        Intent intent = new Intent(MainActivity.this, UpdateActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("object_user", user);
        intent.putExtras(bundle);
        startActivityForResult(intent, MY_REQUEST_CODE);
    }

    private void initUi() {
        edtUsername = (EditText) findViewById(R.id.edt_username);
        edtAddress = (EditText) findViewById(R.id.edt_address);
        btnAddUser = (Button) findViewById(R.id.btn_add_user);
        rcvUser = (RecyclerView) findViewById(R.id.rcv_user);
        
        
        // ánh xạ View chức năng xóa tất cả record
        tvDeleteAll = (TextView) findViewById(R.id.tv_delete_all);
    }

    private void addUser() {
        String strUsername = edtUsername.getText().toString().trim();
        String strAddress = edtAddress.getText().toString().trim();

        if (TextUtils.isEmpty(strUsername) || TextUtils.isEmpty(strAddress)) {
            return;
        }

        User user = new User(strUsername, strAddress);

        if (isUserExist(user)) {
            notifyToast(MainActivity.this, "User exist");
            return;
        }

        UserDatabase.getInstance(MainActivity.this).userDAO().insertUser(user);
        notifyToast(MainActivity.this, "Add user successfully");

        edtUsername.setText("");
        edtAddress.setText("");

        hideSoftKeyboard();

        loadData();
    }

    private void loadData() {
        mListUser =  UserDatabase.getInstance(MainActivity.this).userDAO().getListUser();
        userAdapter.setData(mListUser);
    }

    private boolean isUserExist(User user) {
        List<User> list = UserDatabase.getInstance(MainActivity.this).userDAO().checkUser(user.getUsername());
        return list != null && !list.isEmpty();
    }

    private void hideSoftKeyboard() {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    private void notifyToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MY_REQUEST_CODE && resultCode == Activity.RESULT_OK ) {
            loadData();
        }
    }
}
```

### 1.7.3. Tìm Kiếm Record Trong Table Của Database <a id="1.7.3"></a>
________________________________________________________________________________________________________________________
#### Thiết Kế Background Cho EditText Nhập Nội Dung Tìm Kiếm
________________________________________________________________________________________________________________________
- trong thư mục __res/drawable/__ ta khai báo 1 file resource shape dùng làm background cho EditText (để UI ứng dụng thêm phong phú)
- bg_write_border_gray.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android">
    <solid android:color="@color/white"/>
    <stroke android:width="1dp" android:color="@color/gray"/>
    <corners android:radius="12dp"/>
</shape>
```

#### Thiết Kế UI MainActivity - Thêm 1 View EditText Cho Chức Năng Tìm Kiếm
________________________________________________________________________________________________________________________
- trong activity_main.xml, ta thiết kế thêm 1 View EditText dùng để nhập nội dung tìm kiếm, với background của EditText là 
resource drawable vừa thiết kế ở trên
- activity_main.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="16dp"
    tools:context=".MainActivity">

    <EditText
        android:id="@+id/edt_username"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:hint="Username"
        android:textColor="@color/black"
        android:textSize="16sp" />

    <EditText
        android:id="@+id/edt_address"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="14dp"
        android:hint="Address"
        android:textColor="@color/black"
        android:textSize="16sp" />

    <Button
        android:id="@+id/btn_add_user"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="20dp"
        android:background="@color/design_default_color_secondary_variant"
        android:text="Add User"
        android:textColor="@color/white"
        android:textSize="16sp" />

    <TextView
        android:id="@+id/tv_delete_all"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="end"
        android:layout_margin="10dp"
        android:text="Delete All"
        android:textColor="@color/teal_700"
        android:textSize="20sp" />
  
<!--View dùng để nhập nội dung tìm kiếm trong Table của Database-->
<!--  lưu ý: inputType="text" và imeOptions="actionSearch" để softkeyboard sẽ hiển thị 1 Button Search-->
<!--  hoặc nếu không khai báo inputType và imeOptions thì ta phải thiết kế thêm 1 icon sự kiện Search trong EditText-->
    <EditText
        android:id="@+id/edt_search"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:background="@drawable/bg_write_border_gray"
        android:hint="Enter name"
        android:imeOptions="actionSearch"
        android:inputType="text"
        android:padding="10dp"
        android:textColor="@color/black" />

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/rcv_user"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_marginTop="20dp" />

</LinearLayout>
```

#### UserDAO - Xây Dựng Method Trừu Tượng SELECT Trong Table
________________________________________________________________________________________________________________________
- trong @Dao UserDAO ta xây dựng 1 method abstract truy vấn vào Table của Database trả về List các User với tham số truyền vào
là username của User
- UserDAO.java
```java
package com.hienqp.roomdatabasetincodeyoutube.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.hienqp.roomdatabasetincodeyoutube.User;

import java.util.List;

@Dao
public interface UserDAO {
    @Insert
    void insertUser(User user);

    @Query("SELECT * FROM user")
    List<User> getListUser();

    @Query("SELECT * FROM user WHERE username= :username")
    List<User> checkUser(String username);

    @Update
    void updateUser(User user);

    @Delete
    void deleteUser(User user);

    @Query("DELETE FROM user")
    void deleteAllUser();

    @Query("SELECT * FROM user WHERE username LIKE '%' || :name || '%'")
    List<User> searchUser(String name);
}
```

#### Thiết Kế UI MainActivity - Thêm 1 View EditText Cho Chức Năng Tìm Kiếm
________________________________________________________________________________________________________________________
- trong MainActivity.java
  - khai báo ánh xạ EditText Search
  - bắt sự kiện người dùng click vào Button Search trên softkeyboard với method setOnEditorActionListener() thông qua EditText
  - trong method override ta kiểm tra actionId truyền vào có đúng là EditorInfo.IME_ACTION_SEARCH, nếu đúng thực hiện logic search
  - nếu kiểm tra đạt điều kiện, ta gọi đến method (tự xây dựng) bên ngoài thực hiện truy vấn và lấy dữ liệu về từ database
- MainActivity.java
```java
package com.hienqp.roomdatabasetincodeyoutube;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hienqp.roomdatabasetincodeyoutube.database.UserDatabase;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int MY_REQUEST_CODE = 10;
    private EditText edtUsername;
    private EditText edtAddress;
    private Button btnAddUser;
    private RecyclerView rcvUser;
    private TextView tvDeleteAll;
    
    // khai báo EditText Search
    private EditText edtSearch;

    private UserAdapter userAdapter;
    private List<User> mListUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUi();

        userAdapter = new UserAdapter(new UserAdapter.IClickItemUser() {
            @Override
            public void updateUser(User user) {
                clickUpdateUser(user);
            }

            @Override
            public void deleteUser(User user) {
                clickDeleteUser(user);
            }
        });
        mListUser = new ArrayList<>();
        userAdapter.setData(mListUser);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this);
        rcvUser.setLayoutManager(linearLayoutManager);

        rcvUser.setAdapter(userAdapter);
        loadData();

        btnAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addUser();
            }
        });

        tvDeleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickDeleteAllUser();
            }
        });

        // bắt sự kiện của EditText khi người dùng click vào các thành phần IME hệ thống, ví dụ Button Search trên Softkeyboard
        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // kiểm tra nếu đùng người dùng click vào Button Search trên Softkeyboard thì sẽ thực hiện logic search
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // logic search
                    clickSearchUser();
                }
                return false;
            }
        });
    }

    // xây dựng method search user
    private void clickSearchUser() {
        String strKeyword = edtSearch.getText().toString().trim();
        mListUser = new ArrayList<>();
        mListUser = UserDatabase.getInstance(MainActivity.this).userDAO().searchUser(strKeyword);
        userAdapter.setData(mListUser);
        hideSoftKeyboard();
    }

    private void clickDeleteAllUser() {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Confirm Delete All User")
                .setMessage("Are you sure ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // logic delete user trong database
                        UserDatabase.getInstance(MainActivity.this).userDAO().deleteAllUser();
                        notifyToast(MainActivity.this, "Delete all user successfully");

                        loadData();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void clickDeleteUser(User user) {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Confirm Delete User")
                .setMessage("Are you sure ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // logic delete user trong database
                        UserDatabase.getInstance(MainActivity.this).userDAO().deleteUser(user);
                        notifyToast(MainActivity.this, "Delete user successfully");

                        loadData();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void clickUpdateUser(User user) {
        Intent intent = new Intent(MainActivity.this, UpdateActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("object_user", user);
        intent.putExtras(bundle);
        startActivityForResult(intent, MY_REQUEST_CODE);
    }

    private void initUi() {
        edtUsername = (EditText) findViewById(R.id.edt_username);
        edtAddress = (EditText) findViewById(R.id.edt_address);
        btnAddUser = (Button) findViewById(R.id.btn_add_user);
        rcvUser = (RecyclerView) findViewById(R.id.rcv_user);
        tvDeleteAll = (TextView) findViewById(R.id.tv_delete_all);
        
        // ánh xạ EditText Search
        edtSearch = (EditText) findViewById(R.id.edt_search);
    }

    private void addUser() {
        String strUsername = edtUsername.getText().toString().trim();
        String strAddress = edtAddress.getText().toString().trim();

        if (TextUtils.isEmpty(strUsername) || TextUtils.isEmpty(strAddress)) {
            return;
        }

        User user = new User(strUsername, strAddress);

        if (isUserExist(user)) {
            notifyToast(MainActivity.this, "User exist");
            return;
        }

        UserDatabase.getInstance(MainActivity.this).userDAO().insertUser(user);
        notifyToast(MainActivity.this, "Add user successfully");

        edtUsername.setText("");
        edtAddress.setText("");

        hideSoftKeyboard();

        loadData();
    }

    private void loadData() {
        mListUser =  UserDatabase.getInstance(MainActivity.this).userDAO().getListUser();
        userAdapter.setData(mListUser);
    }

    private boolean isUserExist(User user) {
        List<User> list = UserDatabase.getInstance(MainActivity.this).userDAO().checkUser(user.getUsername());
        return list != null && !list.isEmpty();
    }

    private void hideSoftKeyboard() {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    private void notifyToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MY_REQUEST_CODE && resultCode == Activity.RESULT_OK ) {
            loadData();
        }
    }
}
```

## 1.8. Migrating Room Databases <a id="1.8"></a>
________________________________________________________________________________________________________________________
- trong quá trình phát triển ứng dụng, theo thời gian database có thể ta sẽ cần thay đổi cấu trúc của database
  - thêm cột
  - xóa cột
  - thay đổi kiểu dữ liệu của 1 cột nào đó
  - ...
- tất cả các quá trình thay đối cấu trúc database được gọi là Migration Database
- Room cung cấp cho ta 1 khái niệm: Migration (chuyển đổi) là quá trình thay đổi cấu trúc của database thực hiện theo tiêu chuẩn
của Room
- các bước để Migrate Database trong Room (khi có bất kỳ sự thay đổi nào của cấu trúc database)
- trong @Database UserDatabase
  - tăng giá trị version thêm 1 đơn vị trong @Database
  - khai báo 1 Migration để Migrate data trong @Database và thực hiện câu truy vấn thay đổi cấu trúc database
  - gọi addMigrations(Migration) và truyền vào Migration vừa khai báo trong method khởi tạo instance của @Database
- ví dụ ta thêm 1 Column trong Table với tên là year, kiểu String
- User.java
```java
package com.hienqp.roomdatabasetincodeyoutube;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "user")
public class User implements Serializable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;
    @ColumnInfo(name = "username")
    private String username;
    @ColumnInfo(name = "address")
    private String address;
    
    // Column mới trong Table
    @ColumnInfo(name = "year")
    private String year;

    // thay đổi ở Constructor, thêm tham số truyền vào là year
    public User(String username, String address, String year) {
        this.username = username;
        this.address = address;
        this.year = year;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    // getter cho year
    public String getYear() {
        return year;
    }

    // setter cho year
    public void setYear(String year) {
        this.year = year;
    }
}
```

- trong @Database UserDatabase ta thực hiện các bước để Migrate Database, là hoàn thành việc migrate database
- UserDatabase.java
```java
package com.hienqp.roomdatabasetincodeyoutube.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.hienqp.roomdatabasetincodeyoutube.User;

// thay đổi version thành 2
@Database(entities = {User.class}, version = 2)
public abstract class UserDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "user.db";
    private static UserDatabase instanceUserDatabase;

    // khai báo 1 Migration với tham số cho Constructor là oldVersion và newVersion
    private static Migration migration_from_1_to_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // câu lệnh SQL truy vấn Migrate database
            // ALTER TABLE tên_bảng ADD COLUMN tên_cột định_dạng
            database.execSQL("ALTER TABLE user ADD COLUMN year TEXT");
        }
    };

    public static synchronized UserDatabase getInstance(Context context) {
        if (instanceUserDatabase == null) {
            instanceUserDatabase = Room.databaseBuilder(context.getApplicationContext(), UserDatabase.class, DATABASE_NAME)
                    .allowMainThreadQueries() // cho phép setting Query trên MainThread
                    .addMigrations(migration_from_1_to_2) // thêm method addMigrations(Migration) để update database
                    .build();
        }

        return instanceUserDatabase;
    }

    public abstract UserDAO userDAO();
}
```

### 1.8.1. Cập Nhật Lại Ứng Dụng Với Cấu Trúc Database Đã Migrate <a id="1.8.1"></a>
________________________________________________________________________________________________________________________
#### item_user.xml
________________________________________________________________________________________________________________________
- thêm 1 TextView hiển thị nội dung của Column mới sau khi Migrate database
- item_user.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:padding="10dp">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_centerVertical="true"
        android:layout_marginEnd="10dp"
        android:layout_toStartOf="@id/btn_update"
        android:orientation="vertical">

        <TextView
            android:id="@+id/tv_username"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="@string/app_name"
            android:textColor="@color/black"
            android:textSize="16sp" />

        <TextView
            android:id="@+id/tv_address"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="3dp"
            android:text="@string/app_name"
            android:textColor="@color/gray"
            android:textSize="14sp" />
      
<!--      TextView hiển thị year của User trên Item View-->
        <TextView
            android:id="@+id/tv_year"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="3dp"
            android:text="@string/app_name"
            android:textColor="@color/gray"
            android:textSize="14sp" />
    </LinearLayout>

    <Button
        android:id="@+id/btn_update"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_centerVertical="true"
        android:layout_marginEnd="5dp"
        android:layout_toStartOf="@id/btn_delete"
        android:text="Update" />

    <Button
        android:id="@+id/btn_delete"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_alignParentEnd="true"
        android:layout_centerVertical="true"
        android:text="Delete" />

</RelativeLayout>
```

#### UserAdapter.java
________________________________________________________________________________________________________________________
- sau khi thiết kế thêm 1 View hiển thị year của User trên View Item
- ta khai báo ánh xạ View đó trong UserAdapter
- trong onBindViewHolder() ta thêm lệnh set dữ liệu cho TextView year
- UserAdapter.java
```java
package com.hienqp.roomdatabasetincodeyoutube;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder>{
    private List<User> mListUser;
    private IClickItemUser iClickItemUser;

    public UserAdapter(IClickItemUser iClickItemUser) {
        this.iClickItemUser = iClickItemUser;
    }

    public void setData(List<User> list) {
        UserAdapter.this.mListUser = list;
        notifyDataSetChanged();
    }

    public interface IClickItemUser {
        void updateUser(User user);
        void deleteUser(User user);
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        final User user = mListUser.get(position);
        if (user == null) {
            return;
        }

        holder.tvUsername.setText(user.getUsername());
        holder.tvAddress.setText(user.getAddress());

        // set data cho TextView year
        holder.tvYear.setText(user.getYear());

        holder.btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iClickItemUser.updateUser(user);
            }
        });
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iClickItemUser.deleteUser(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mListUser != null) {
            return mListUser.size();
        }
        return 0;
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        private TextView tvUsername;
        private TextView tvAddress;
        private Button btnUpdate;
        private Button btnDelete;
        
        // khai báo TextView year
        private TextView tvYear;


        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            tvUsername = (TextView) itemView.findViewById(R.id.tv_username);
            tvAddress = (TextView) itemView.findViewById(R.id.tv_address);
            btnUpdate = (Button) itemView.findViewById(R.id.btn_update);
            btnDelete = (Button) itemView.findViewById(R.id.btn_delete);

            // ánh xạ TextView year
            tvYear = (TextView) itemView.findViewById(R.id.tv_year);
        }
    }
}
```

#### activity_main.xml
________________________________________________________________________________________________________________________
- trong activity_main.xml ta thiết kế thêm 1 EditText để người dùng nhập year vào database
- activity_main.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="16dp"
    tools:context=".MainActivity">

    <EditText
        android:id="@+id/edt_username"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:hint="Username"
        android:textColor="@color/black"
        android:textSize="16sp" />

    <EditText
        android:id="@+id/edt_address"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="14dp"
        android:hint="Address"
        android:textColor="@color/black"
        android:textSize="16sp" />
  
<!--EditText cho người dùng nhập dữ liệu year vào database-->
    <EditText
        android:id="@+id/edt_year"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="14dp"
        android:hint="Year"
        android:textColor="@color/black"
        android:textSize="16sp" />

    <Button
        android:id="@+id/btn_add_user"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="20dp"
        android:background="@color/design_default_color_secondary_variant"
        android:text="Add User"
        android:textColor="@color/white"
        android:textSize="16sp" />

    <TextView
        android:id="@+id/tv_delete_all"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="end"
        android:layout_margin="10dp"
        android:text="Delete All"
        android:textColor="@color/teal_700"
        android:textSize="20sp" />

    <EditText
        android:id="@+id/edt_search"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:background="@drawable/bg_write_border_gray"
        android:hint="Enter name"
        android:imeOptions="actionSearch"
        android:inputType="text"
        android:padding="10dp"
        android:textColor="@color/black" />

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/rcv_user"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_marginTop="20dp" />

</LinearLayout>
```

#### MainActivity.java
________________________________________________________________________________________________________________________
- trong MainActivity.java tiến hành 
  - khai báo ánh xạ EditText year
  - thay đổi 1 vài vị trí trong method addUser() của MainActivity.java
- MainActivity.java
```java
package com.hienqp.roomdatabasetincodeyoutube;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hienqp.roomdatabasetincodeyoutube.database.UserDatabase;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
  private static final int MY_REQUEST_CODE = 10;
  private EditText edtUsername;
  private EditText edtAddress;
  private Button btnAddUser;
  private RecyclerView rcvUser;
  private TextView tvDeleteAll;
  private EditText edtSearch;
  
  // khai báo EditText Year
  private EditText edtYear;

  private UserAdapter userAdapter;
  private List<User> mListUser;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    initUi();

    userAdapter = new UserAdapter(new UserAdapter.IClickItemUser() {
      @Override
      public void updateUser(User user) {
        clickUpdateUser(user);
      }

      @Override
      public void deleteUser(User user) {
        clickDeleteUser(user);
      }
    });
    mListUser = new ArrayList<>();
    userAdapter.setData(mListUser);

    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this);
    rcvUser.setLayoutManager(linearLayoutManager);

    rcvUser.setAdapter(userAdapter);
    loadData();

    btnAddUser.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        addUser();
      }
    });

    tvDeleteAll.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        clickDeleteAllUser();
      }
    });

    edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      @Override
      public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
          // logic search
          clickSearchUser();
        }
        return false;
      }
    });
  }

  private void clickSearchUser() {
    String strKeyword = edtSearch.getText().toString().trim();
    mListUser = new ArrayList<>();
    mListUser = UserDatabase.getInstance(MainActivity.this).userDAO().searchUser(strKeyword);
    userAdapter.setData(mListUser);
    hideSoftKeyboard();
  }

  private void clickDeleteAllUser() {
    new AlertDialog.Builder(MainActivity.this)
            .setTitle("Confirm Delete All User")
            .setMessage("Are you sure ?")
            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                // logic delete user trong database
                UserDatabase.getInstance(MainActivity.this).userDAO().deleteAllUser();
                notifyToast(MainActivity.this, "Delete all user successfully");

                loadData();
              }
            })
            .setNegativeButton("No", null)
            .show();
  }

  private void clickDeleteUser(User user) {
    new AlertDialog.Builder(MainActivity.this)
            .setTitle("Confirm Delete User")
            .setMessage("Are you sure ?")
            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                // logic delete user trong database
                UserDatabase.getInstance(MainActivity.this).userDAO().deleteUser(user);
                notifyToast(MainActivity.this, "Delete user successfully");

                loadData();
              }
            })
            .setNegativeButton("No", null)
            .show();
  }

  private void clickUpdateUser(User user) {
    Intent intent = new Intent(MainActivity.this, UpdateActivity.class);
    Bundle bundle = new Bundle();
    bundle.putSerializable("object_user", user);
    intent.putExtras(bundle);
    startActivityForResult(intent, MY_REQUEST_CODE);
  }

  private void initUi() {
    edtUsername = (EditText) findViewById(R.id.edt_username);
    edtAddress = (EditText) findViewById(R.id.edt_address);
    btnAddUser = (Button) findViewById(R.id.btn_add_user);
    rcvUser = (RecyclerView) findViewById(R.id.rcv_user);
    tvDeleteAll = (TextView) findViewById(R.id.tv_delete_all);
    edtSearch = (EditText) findViewById(R.id.edt_search);
    
    // ánh xạ EditText Year
    edtYear = (EditText) findViewById(R.id.edt_year);
  }

  private void addUser() {
    String strUsername = edtUsername.getText().toString().trim();
    String strAddress = edtAddress.getText().toString().trim();
    
    // lấy dữ liệu người dùng nhập vào EditText Year
    String strYear = edtYear.getText().toString().trim();

    if (TextUtils.isEmpty(strUsername) || TextUtils.isEmpty(strAddress)) {
      return;
    }

    // thêm tham số cho constructor User
    User user = new User(strUsername, strAddress, strYear);

    if (isUserExist(user)) {
      notifyToast(MainActivity.this, "User exist");
      return;
    }

    UserDatabase.getInstance(MainActivity.this).userDAO().insertUser(user);
    notifyToast(MainActivity.this, "Add user successfully");

    edtUsername.setText("");
    edtAddress.setText("");
    
    // xóa nội dung trên EditText Year sau khi addUser thành công
    edtYear.setText("");

    hideSoftKeyboard();

    loadData();
  }

  private void loadData() {
    mListUser =  UserDatabase.getInstance(MainActivity.this).userDAO().getListUser();
    userAdapter.setData(mListUser);
  }

  private boolean isUserExist(User user) {
    List<User> list = UserDatabase.getInstance(MainActivity.this).userDAO().checkUser(user.getUsername());
    return list != null && !list.isEmpty();
  }

  private void hideSoftKeyboard() {
    try {
      InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
      inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    } catch (NullPointerException ex) {
      ex.printStackTrace();
    }
  }

  private void notifyToast(Context context, String message) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == MY_REQUEST_CODE && resultCode == Activity.RESULT_OK ) {
      loadData();
    }
  }
}
```

### 1.8.2. Một Vài Câu Lệnh SQL Hữu Ích <a id="1.8.2"></a>
________________________________________________________________________________________________________________________
- thêm 1 cột: 
  - __"ALTER TABLE Tên_table ADD COLUMN Tên_column Kiểu_data_của_cột"__
  - ``database.execSQL("ALTER TABLE user ADD COLUMN year TEXT");``
- thêm nhiều cột:
  - __"ALTER TABLE Tên_table ADD Tên_cột_1 Kiểu_data_cột_1, Tên_cột_2 Kiểu_data_cột_2, ..."__
- thay đổi kiểu data của cột:
  - __"ALTER TABLE Tên_table ALTER Tên_cột Kiểu_data_mới"__
- xóa 1 cột:
  - __"ALTER TABLE Tên_table DROP COLUMN Tên_cột"__