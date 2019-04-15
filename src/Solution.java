import java.io.*;
import java.util.*;
import java.text.*;
import java.math.*;
import java.util.regex.*;

public class Solution {
    

    public static void main(String[] args) throws IOException {
        /* Enter your code here. Read input from STDIN. Print output to STDOUT. Your class should be named Solution. */
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter output = new BufferedWriter(new OutputStreamWriter(System.out, "ASCII"), 4096);
        int queryCount = Integer.parseInt(br.readLine());
        
        // grab entries per line into a String ArrayList
        List<String> entries = new ArrayList<String>();
        for(int i=0; i<queryCount; i++){ 	
            entries.add(br.readLine());
        }

        TwoThreeTree tree = new TwoThreeTree();
        for(int i = 0; i < queryCount; i++) {
            //length 2 array with name and cost of planet
            String[] data = entries.get(i).split("\\s"); 
            // do a thing depending on the query type
            switch(Integer.parseInt(data[0])){
            
            case 1:
            	twothree.insert(data[1], Integer.parseInt(data[2]), tree);
            	break;
            case 2:
            	if(data[1].compareTo(data[2]) <= 0){
                	twothree.addRange(tree.root, data[1], data[2], tree.height, Integer.parseInt(data[3]));
            	}
            	else{
                    twothree.addRange(tree.root, data[2], data[1], tree.height, Integer.parseInt(data[3]));
                }
            	break;
            case 3:
            	output.write(twothree.search(tree.root, data[1], tree.height, 0) + "\n");
            	break;
            default: 
            	output.write("Error: Query code must be 1, 2 or 3.");
            	break;
            }
            
        }
        output.flush();
    }
}

class twothree {

    static void insert(String key, int value, TwoThreeTree tree) {
    // insert a key value pair into tree (overwrite existing value
    // if key is already present)

      int h = tree.height;

      if (h == -1) {
          LeafNode newLeaf = new LeafNode();
          newLeaf.guide = key;
          newLeaf.value = value;
          tree.root = newLeaf; 
          tree.height = 0;
      }
      else {
         WorkSpace ws = doInsert(key, value, tree.root, h);

         if (ws != null && ws.newNode != null) {
         // create a new root

            InternalNode newRoot = new InternalNode();
            if (ws.offset == 0) {
               newRoot.child0 = ws.newNode; 
               newRoot.child1 = tree.root;
            }
            else {
               newRoot.child0 = tree.root; 
               newRoot.child1 = ws.newNode;
            }
            resetGuide(newRoot);
            tree.root = newRoot;
            tree.height = h+1;
         }
      }
    }

    static WorkSpace doInsert(String key, int value, Node p, int h) {
    // auxiliary recursive routine for insert

      if (h == 0) {
         // we're at the leaf level, so compare and 
         // either update value or insert new leaf

         LeafNode leaf = (LeafNode) p; //downcast
         int cmp = key.compareTo(leaf.guide);

         if (cmp == 0) {
            leaf.value = value; 
            return null;
         }

         // create new leaf node and insert into tree
         LeafNode newLeaf = new LeafNode();
         newLeaf.guide = key; 
         newLeaf.value = value;

         int offset = (cmp < 0) ? 0 : 1;
         // offset == 0 => newLeaf inserted as left sibling
         // offset == 1 => newLeaf inserted as right sibling

         WorkSpace ws = new WorkSpace();
         ws.newNode = newLeaf;
         ws.offset = offset;
         ws.scratch = new Node[4];
         

         return ws;
      }
      else {
         InternalNode q = (InternalNode) p; // downcast
         int pos;
         WorkSpace ws;
         //shoveling
         if(q.value != 0){
        	 q.child0.value += q.value;
        	 q.child1.value += q.value;
        	 if(q.child2 != null) q.child2.value += q.value;
         }
         q.value = 0;
         if (key.compareTo(q.child0.guide) <= 0) {
            pos = 0; 
            ws = doInsert(key, value, q.child0, h-1);
         }
         else if (key.compareTo(q.child1.guide) <= 0 || q.child2 == null) {
            pos = 1;
            ws = doInsert(key, value, q.child1, h-1);
         }
         else {
            pos = 2; 
            ws = doInsert(key, value, q.child2, h-1);
         }

         if (ws != null) {
            if (ws.newNode != null) {
               // make ws.newNode child # pos + ws.offset of q

               int sz = copyOutChildren(q, ws.scratch);
               insertNode(ws.scratch, ws.newNode, sz, pos + ws.offset);
               if (sz == 2) {
                  ws.newNode = null;
                  ws.guideChanged = resetChildren(q, ws.scratch, 0, 3);
               }
               else {
                  ws.newNode = new InternalNode();
                  ws.offset = 1;
                  resetChildren(q, ws.scratch, 0, 2);
                  resetChildren((InternalNode) ws.newNode, ws.scratch, 2, 2);
               }
            }
            else if (ws.guideChanged) {
               ws.guideChanged = resetGuide(q);
            }
         }

         return ws;
      }
    }


    static int copyOutChildren(InternalNode q, Node[] x) {
    // copy children of q into x, and return # of children

      int sz = 2;
      x[0] = q.child0; x[1] = q.child1;
      if (q.child2 != null) {
         x[2] = q.child2; 
         sz = 3;
      }
      return sz;
    }

    static void insertNode(Node[] x, Node p, int sz, int pos) {
    // insert p in x[0..sz) at position pos,
    // moving existing extries to the right

      for (int i = sz; i > pos; i--)
         x[i] = x[i-1];

      x[pos] = p;
    }

    static boolean resetGuide(InternalNode q) {
    // reset q.guide, and return true if it changes.

      String oldGuide = q.guide;
      if (q.child2 != null)
         q.guide = q.child2.guide;
      else
         q.guide = q.child1.guide;

      return q.guide != oldGuide;
    }


    static boolean resetChildren(InternalNode q, Node[] x, int pos, int sz) {
    // reset q's children to x[pos..pos+sz), where sz is 2 or 3.
    // also resets guide, and returns the result of that

      q.child0 = x[pos]; 
      q.child1 = x[pos+1];

      if (sz == 3) 
         q.child2 = x[pos+2];
      else
         q.child2 = null;

      return resetGuide(q);
    }
    
    //debug function
    static void printEverything(Node node, int h, BufferedWriter output) throws IOException{
    	output.write("current height is " + h + "\n");
    	output.write("node guide is " + node.guide + "\n");
    	output.write("node value is " + node.value + "\n");
    	if(node instanceof InternalNode){
    		InternalNode p = (InternalNode) node;
    		if (p.child2 == null){
    			output.write("node has children: " + p.child0.guide + " " + p.child1.guide + "\n");
    		}
    		else{
    			output.write("node has children: " + p.child0.guide + " " + p.child1.guide + " " + p.child2.guide + "\n");
    		}
    		
    		output.write("=========================\n");

    		printEverything(p.child0, h-1, output);
    		printEverything(p.child1, h-1, output);
            if(p.child2 != null){
            	printEverything(p.child2, h-1, output);
            }
    	}
    	else{
    		output.write("=========================\n");
    	}
    	
    }
    
    static void addAll(Node node, int h, int val){
    	node.value += val;
    }
    

    static void addGE(Node node, String x, int h, int val){
        //base case
        if(h == 0 && node instanceof LeafNode) {
            LeafNode p = (LeafNode)node;
            if(p.guide.compareTo(x) >= 0){
            	p.value += val;
            }
        }
        else if (node instanceof InternalNode){
            InternalNode p = (InternalNode) node;
            if (p.child0.guide.compareTo(x) >= 0){
                addGE(p.child0, x, h-1, val);
                addAll(p.child1, h-1, val);
                if(p.child2 != null){
                    addAll(p.child2, h-1, val);
                }
            }
            else if (p.child2 == null || p.child1.guide.compareTo(x) >= 0){
                addGE(p.child1, x, h-1, val);
                if(p.child2 != null) addAll(p.child2, h-1, val);
            }
            else{
                addGE(p.child2, x, h-1, val);
            }
        }
    }
    

    static void addLE(Node node, String x, int h, int val){
        //base case
        if(h == 0 && node instanceof LeafNode) {
            LeafNode p = (LeafNode)node;
            if(p.guide.compareTo(x) <= 0){
                p.value += val;
            }
        }
        else if (node instanceof InternalNode){
            InternalNode p = (InternalNode) node;
            if (p.child0.guide.compareTo(x) >= 0){
                addLE(p.child0, x, h-1, val);
            }
            else if (p.child2 == null || p.child1.guide.compareTo(x) >= 0){
                addAll(p.child0, h-1, val);
                addLE(p.child1, x, h-1, val);
            }
            else{
            	addAll(p.child0, h-1, val);
            	addAll(p.child1, h-1, val);
            	addLE(p.child2, x, h-1, val);
            }
        }
    }
    
    
    static void addRange(Node node, String x, String y, int h, int val){
    	if(h==0 && node instanceof LeafNode){
            LeafNode p = (LeafNode)node;
            if(p.guide.compareTo(x) >= 0 && p.guide.compareTo(y) <= 0){
                p.value += val;
            }
        }
        else if(node instanceof InternalNode){
            InternalNode p = (InternalNode) node;
            //if true, lower bound is within child 0 
            if(p.child0.guide.compareTo(x) >= 0){
                addRange(p.child0, x, y, h-1, val);
                addLE(p.child1, y, h-1, val);
                if(p.child2 != null) addLE(p.child2, y, h-1, val);    
            }
            //if true, lower bound within child1
            else if (p.child2 == null || p.child1.guide.compareTo(x) >= 0){
                addRange(p.child1, x, y, h-1, val);
                if(p.child2 != null) addLE(p.child2, y, h-1, val);
            }
            else {
            	addRange(p.child2, x, y, h-1, val);
            }
        }
    }
    
    static int search(Node node, String key, int h, int run){
    	// if (node.guide.compareTo(key)<0) return -1;
    	if(h==0){
    		if(node.guide.equals(key)){
    			return run + node.value;
    		}

    	}
    	
    	else if (node instanceof InternalNode){
    		InternalNode p = (InternalNode) node;
    		if(p.child0.guide.compareTo(key)>=0){
    			run += p.value;
    			return search(p.child0, key, h-1, run);
    		}
    		else if(p.child2 == null || p.child1.guide.compareTo(key)>=0){
    			run += p.value;
    			return search(p.child1, key, h-1, run);
    		}
    		
    		else{
    			run += p.value;
    			return search(p.child2, key, h-1, run);
    		}
    	}
    	return -1;
    }
}


class Node {
   String guide;
   int value;
   // guide points to max key in subtree rooted at node
}

class InternalNode extends Node {
   Node child0, child1, child2;
   // child0 and child1 are always non-null
   // child2 is null iff node has only 2 children
}

class LeafNode extends Node {
   // guide points to the key

}

class TwoThreeTree {
   Node root;
   int height;

   TwoThreeTree() {
      root = null;
      height = -1;
   }
}

class WorkSpace {
// this class is used to hold return values for the recursive doInsert
// routine

   Node newNode;
   int offset;
   boolean guideChanged;
   Node[] scratch;
}





