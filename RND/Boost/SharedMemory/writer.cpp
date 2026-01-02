#include <boost/container/vector.hpp>
#include <boost/interprocess/allocators/allocator.hpp>
#include <boost/interprocess/managed_shared_memory.hpp>

#include <iostream>

#include "data.h"

int main ()
{
   using namespace boost::interprocess;
   //Remove shared memory on construction and destruction
   struct shm_remove
   {
      shm_remove() { shared_memory_object::remove("MyName"); }
      ~shm_remove(){ shared_memory_object::remove("MyName"); }
   } remover;

   //A managed shared memory where we can construct objects
   //associated with a c-string
   managed_shared_memory segment(create_only,"MyName", 65536);

   //Alias an STL-like allocator of ints that allocates ints from the segment
   typedef allocator<sdata_t, managed_shared_memory::segment_manager>
      ShmemAllocator;

   //Alias a vector that uses the previous STL-like allocator
   typedef boost::container::vector<sdata_t, ShmemAllocator> MyVector;

   //Construct the vector in the shared memory segment with the STL-like allocator
   //from a range of iterators
   MyVector *myvector = segment.construct<MyVector>("MyVector")(segment.get_segment_manager());

   sdata_t d;
   while(d.n >= 0)
   {
      std::cout << "string: ";
      std::cin >> d.s;

      std::cout << "Type a number >=0; < 0 to quit: ";
      std::cin >> d.n;

      myvector->push_back(d);
   }
   
   segment.destroy<MyVector>("MyVector");
   return 0;
}